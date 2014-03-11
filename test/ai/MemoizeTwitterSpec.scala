package ai

import java.util.concurrent.{TimeUnit, CountDownLatch}
import java.util.concurrent.atomic.AtomicInteger
import scala.annotation.tailrec
import org.scalatest.FunSuite
import org.mockito.Mockito._
import com.twitter.util._
import com.twitter.conversions.time._
import com.twitter.util.Throw

// The code below is originally based on com.twitter.util.Memoize. I changed it from an object to a class.
class MemoizeTwitter[A, B](f: A => B) {
  /**
   * Thread-safe memoization for a function.
   *
   * This works like a lazy val indexed by the input value. The memo
   * is held as part of the state of the returned function, so keeping
   * a reference to the function will keep a reference to the
   * (unbounded) memo table. The memo table will never forget a
   * result, and will retain a reference to the corresponding input
   * values as well.
   *
   * If the computation has side-effects, they will happen exactly
   * once per input, even if multiple threads attempt to memoize the
   * same input at one time, unless the computation throws an
   * exception. If an exception is thrown, then the result will not be
   * stored, and the computation will be attempted again upon the next
   * access. Only one value will be computed at a time. The overhead
   * required to ensure that the effects happen only once is paid only
   * in the case of a miss (once per input over the life of the memo
   * table). Computations for different input values will not block
   * each other.
   *
   * The combination of these factors means that this method is useful
   * for functions that will only ever be called on small numbers of
   * inputs, are expensive compared to a hash lookup and the memory
   * overhead, and will be called repeatedly.
   */
  private var memo = Map.empty[A, Either[CountDownLatch, B]]


  /**
   * What to do if we do not find the value already in the memo
   * table.
   */
  @tailrec private[this] def missing(a: A): B =
    synchronized {
      // With the lock, check to see what state the value is in.
      memo.get(a) match {
        case None =>
          // If it's missing, then claim the slot by putting in a
          // CountDownLatch that will be completed when the value is
          // available.
          val latch = new CountDownLatch(1)
          memo = memo + (a -> Left(latch))

          // The latch wrapped in Left indicates that the value
          // needs to be computed in this thread, and then the
          // latch counted down.
          Left(latch)

        case Some(other) =>
          // This is either the latch that will indicate that the
          // work has been done, or the computed value.
          Right(other)
      }
    } match {
      case Right(Right(b)) => b // The computation is already done.
      case Right(Left(latch)) =>
        // Someone else is doing the computation.
        latch.await()

        // This recursive call will happen when there is an
        // exception computing the value, or if the value is
        // currently being computed.
        missing(a)

      case Left(latch) =>
        // Compute the value outside of the synchronized block.
        val b =
          try {
            f(a)
          } catch {
            case t: Throwable =>
              // If there was an exception running the
              // computation, then we need to make sure we do not
              // starve any waiters before propagating the
              // exception.
              synchronized {
                memo = memo - a
              }
              latch.countDown()
              throw t
          }

        // Update the memo table to indicate that the work has
        // been done, and signal to any waiting threads that the
        // work is complete.
        synchronized {
          memo = memo + (a -> Right(b))
        }
        latch.countDown()
        b
    }

  def getOrElseUpdate(a: A): B =
  // Look in the (possibly stale) memo table. If the value is
  // present, then it is guaranteed to be the final value. If it
  // is absent, call missing() to determine what to do.
    memo.get(a) match {
      case Some(Right(b)) => b
      case _ => missing(a)
    }
}

class MemoizeTwitterSpec extends FunSuite {
  test("Memoize.apply: only runs the function once for the same input") {
    // mockito can't spy anonymous classes,
    // and this was the simplest approach i could come up with.
    class Adder extends (Int => Int) {
      override def apply(i: Int) = i + 1
    }

    val adder = spy(new Adder)
    val memoizer = new MemoizeTwitter(f = adder(_: Int))

    assert(2 === memoizer.getOrElseUpdate(1))
    assert(2 === memoizer.getOrElseUpdate(1))
    assert(3 === memoizer.getOrElseUpdate(2))

    verify(adder, times(1))(1)
    verify(adder, times(1))(2)
  }

  test("Memoize.apply: only executes the memoized computation once per input") {
    val callCount = new AtomicInteger(0)
    val startUpLatch = new CountDownLatch(1)

    class Incrementer extends (Int => String) {
      override def apply(i: Int) = {
        // Wait for all of the threads to be started before
        // continuing. This gives races a chance to happen.
        startUpLatch.await()

        // Perform the effect of incrementing the counter, so that we
        // can detect whether this code is executed more than once.
        callCount.incrementAndGet()

        // Return a new object so that object equality will not pass
        // if two different result values are used.
        "." * i
      }
    }

    val incrementer = spy(new Incrementer)
    val memoizer = new MemoizeTwitter[Int, String](f = incrementer(_))

    val ConcurrencyLevel = 5
    val computations =
      Future.collect(1 to ConcurrencyLevel map {
        _ =>
          FuturePool.unboundedPool(memoizer.getOrElseUpdate(5))
      })

    startUpLatch.countDown()
    val results = Await.result(computations)

    // All of the items are equal, up to reference equality
    results foreach {
      item =>
        assert(item === results(0))
        assert(item eq results(0))
    }

    // The effects happen exactly once
    assert(callCount.get() === 1)
  }

  test("Memoize.apply: handles exceptions during computations") {
    val TheException = new RuntimeException
    val startUpLatch = new CountDownLatch(1)
    val callCount = new AtomicInteger(0)

    class FailFirstTime extends (Int => Int) {
      override def apply(i: Int) = {
        // Ensure that all of the callers have been started
        startUpLatch.await(200, TimeUnit.MILLISECONDS)
        // This effect should happen once per exception plus once for
        // all successes
        val n = callCount.incrementAndGet()
        if (n == 1) throw TheException else i + 1
      }
    }

    // A computation that should fail the first time, and then
    // succeed for all subsequent attempts.
    val failFirstTime = spy(new FailFirstTime)
    val memo = new MemoizeTwitter[Int, Int](f = failFirstTime(_))


    val ConcurrencyLevel = 5
    val computation =
      Future.collect(1 to ConcurrencyLevel map {
        _ =>
          FuturePool.unboundedPool(memo.getOrElseUpdate(5)) transform {
            Future.value
          }
      })

    startUpLatch.countDown()
    val (successes, failures) =
      Await.result(computation, 200.milliseconds).toList partition {
        _.isReturn
      }

    // One of the times, the computation must have failed.
    assert(failures === List(Throw(TheException)))

    // Another time, it must have succeeded, and then the stored
    // result will be reused for the other calls.
    assert(successes === List.fill(ConcurrencyLevel - 1)(Return(6)))

    // The exception plus another successful call:
    assert(callCount.get() === 2)
  }
}

