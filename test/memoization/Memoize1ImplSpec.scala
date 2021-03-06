package memoization

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import com.twitter.conversions.time._
import com.twitter.util._
import composition.UnitTestHelpers
import org.mockito.Mockito._
import play.api.libs.json.Json._
import play.api.libs.json._

import scala.annotation.tailrec

final class Memoize1ImplSpec extends UnitTestHelpers {

  // TODO remove twitter Futures, use to scala futures
  "apply" must {
    "return the same result when called twice" in {
      val memoizePlusOne = new Memoize1Impl[Int, Int] {
        def f(i: Int): Int = i + 1
      }

      memoizePlusOne(1) must equal(2)
      memoizePlusOne(1) must equal(2)
    }

    "only runs the function once for the same input (fibonacci recursive)" in {
      class Fib {

        def apply(i: Int): Int = fib(i)

        private def fib(i: Int): Int = i match {
          case 0 => 0
          case 1 => 1
          case _ => fib(i - 1) + fib(i - 2)
        }
      }

      val fib = spy(new Fib)
      val memoizer = new Memoize1Impl[Int, Int] {
        def f(i: Int): Int = fib(i)
      }

      memoizer(1) must equal(1)
      memoizer(1) must equal(1)
      memoizer(2) must equal(1)
      memoizer(2) must equal(1)
      memoizer(3) must equal(2)
      memoizer(3) must equal(2)

      verify(fib, times(1))(1)
      verify(fib, times(1))(2)
      verify(fib, times(1))(3)
      verifyNoMoreInteractions(fib)
    }

    "only runs the function once for the same input (fibonacci tail recursive)" in {
      class Fib {

        def apply(i: Int): Int = fib(i)

        @tailrec private def fib(i: Int, a: Int = 1, b: Int = 0): Int = i match {
          case 0 => b
          case _ => fib(i - 1, b, a + b)
        }
      }

      val fib = spy(new Fib)
      val memoizer = new Memoize1Impl[Int, Int] {
        def f(i: Int): Int = fib(i)
      }

      memoizer(1) must equal(1)
      memoizer(1) must equal(1)
      memoizer(2) must equal(1)
      memoizer(2) must equal(1)
      memoizer(3) must equal(2)
      memoizer(3) must equal(2)

      verify(fib, times(1))(1)
      verify(fib, times(1))(2)
      verify(fib, times(1))(3)
      verifyNoMoreInteractions(fib)
    }

    "only runs the function once for the same input (adder)" in {
      // mockito can't spy anonymous classes,
      // and this was the simplest approach i could come up with.
      class Adder extends (Int => Int) {

        override def apply(i: Int) = i + 1
      }

      val adder = spy(new Adder)
      val memoizePlusOne = new Memoize1Impl[Int, Int] {
        def f(i: Int): Int = adder(i)
      }

      memoizePlusOne(1) must equal(2)
      memoizePlusOne(1) must equal(2)
      memoizePlusOne(2) must equal(3)
      memoizePlusOne(2) must equal(3)

      verify(adder, times(1))(1)
      verify(adder, times(1))(2)
      verifyNoMoreInteractions(adder)
    }

    "only executes the memoized computation once per input" in {
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
      val memoizeIncrementer = new Memoize1Impl[Int, String] {
        def f(i: Int): String = incrementer(i)
      }

      val ConcurrencyLevel = 5
      val computations =
        Future.collect(1 to ConcurrencyLevel map {
          _ =>
            FuturePool.unboundedPool(memoizeIncrementer(5))
        })

      startUpLatch.countDown()
      val results = Await.result(computations)

      // All of the items are equal, up to reference equality
      results foreach {
        item =>
          val result = results(0)
          result must equal(item)
          result must be theSameInstanceAs item
      }

      // The effects happen exactly once
      callCount.get() must equal(1)
    }

    "handles exceptions during computations" in {
      val TheException = new RuntimeException
      val startUpLatch = new CountDownLatch(1)
      val callCount = new AtomicInteger(0)

      class FailFirstTime extends (Int => Int) {

        override def apply(i: Int) = {
          // Ensure that all of the callers have been started
          startUpLatch.await(200, TimeUnit.MILLISECONDS)
          // This effect must happen once per exception plus once for
          // all successes
          val n = callCount.incrementAndGet()
          if (n == 1) throw TheException else i + 1
        }
      }

      // A computation that must fail the first time, and then
      // succeed for all subsequent attempts.
      val failFirstTime = spy(new FailFirstTime)
      val memoizeFailFirstTime = new Memoize1Impl[Int, Int] {
        def f(i: Int): Int = failFirstTime(i)
      }

      val ConcurrencyLevel = 5
      val computation =
        Future.collect(1 to ConcurrencyLevel map {
          _ =>
            FuturePool.unboundedPool(memoizeFailFirstTime(5)) transform {
              Future.value
            }
        })

      startUpLatch.countDown()

      val (successes, failures) =
        Await.result(computation, 2.seconds).toList partition {
          _.isReturn
        }

      // One of the times, the computation must have failed.
      failures must equal(List(Throw(TheException)))

      // Another time, it must have succeeded, and then the stored
      // result will be reused for the other calls.
      successes must equal(List.fill(ConcurrencyLevel - 1)(Return(6)))

      // The exception plus another successful call:
      callCount.get() must equal(2)
    }
  }

  "write" must {
    "turn map into Json" in {
      val memoizeFib = new Memoize1Impl[Int, Int] {
        def f(i: Int): Int = i match {
          case 0 => 0
          case 1 => 1
          case _ => missing(i - 1) + missing(i - 2)
        }
      }

      memoizeFib(1) must equal(1)
      memoizeFib(2) must equal(1)
      memoizeFib(3) must equal(2)

      memoizeFib.write must equal(
        JsObject(
          Seq(
            ("1",
              JsObject(
                Seq(
                  ("intContent", JsNumber(1))
                )
              )
            ),
            ("2",
              JsObject(
                Seq(
                  ("intContent", JsNumber(1))
                )
              )
            ),
            ("0",
              JsObject(
                Seq(
                  ("intContent", JsNumber(0))
                )
              )
            ),
            ("3",
              JsObject(
                Seq(
                  ("intContent", JsNumber(2))
                )
              )
            )
          )
        )

      )
    }
  }

  "size" must {
    "return 0 when empty" in {
      val memoizePlusOne = new Memoize1Impl[Int, Int] {
        def f(i: Int): Int = i + 1
      }

      memoizePlusOne.size must equal(0)
    }
    "return 1 when only one entry" in {
      val memoizePlusOne = new Memoize1Impl[Int, Int] {
        def f(i: Int): Int = i + 1
      }

      memoizePlusOne(1)

      memoizePlusOne.size must equal(1)
    }
    "return 3 when 3 entries" in {
      val memoizePlusOne = new Memoize1Impl[Int, Int] {
        def f(i: Int): Int = i + 1
      }

      memoizePlusOne(1)
      memoizePlusOne(2)
      memoizePlusOne(3)

      memoizePlusOne.size must equal(3)
    }
  }

  private implicit val eitherLatchOrIntToJson = new Writes[Either[CountDownLatch, Int]] {
    def writes(o: Either[CountDownLatch, Int]): JsValue = obj(
      o.fold(
        countDownLatchContent => ???, // must be filtered out at a higher level so that we do not store incomplete calculations.
        intContent => "intContent" -> Json.toJson(intContent)
      )
    )
  }

  private implicit val mapOfIntNeighboursToJson = new Writes[Map[Int, Either[CountDownLatch, Int]]] {
    def writes(o: Map[Int, Either[CountDownLatch, Int]]): JsValue = {
      val filtered = o.
        filter(kvp => kvp._2.isRight). // Only completed values.
        map(kvp => kvp._1.toString -> kvp._2) // Key must be string

      Json.toJson(filtered)
    }
  }

  private implicit val eitherLatchOrStringToJson = new Writes[Either[CountDownLatch, String]] {
    def writes(o: Either[CountDownLatch, String]): JsValue = obj(
      o.fold(
        countDownLatchContent => ???, // must be filtered out at a higher level so that we do not store incomplete calculations.
        intContent => "strContent" -> Json.toJson(intContent.toString)
      )
    )
  }

  private implicit val mapOfStringNeighboursToJson = new Writes[Map[Int, Either[CountDownLatch, String]]] {
    def writes(o: Map[Int, Either[CountDownLatch, String]]): JsValue = {
      val filtered = o.filter {
        case (k, v) => v.isRight // Only completed values.
      }.
        map {
          case (k, v) => k.toString -> v // Key must be string
        }

      Json.toJson(filtered)
    }
  }
}