package nodes.legalNeighbours

import java.util.concurrent.TimeUnit
import scala.annotation.tailrec
import org.scalatest.{WordSpec, Matchers}
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import com.twitter.util._
import com.twitter.conversions.time._
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger
import nodes.helpers.JsonSerialiser
import com.twitter.util.Throw
import scala.collection.immutable.BitSet
import play.api.libs.json._


class MemoizeSpec extends WordSpec with Matchers with ScalaFutures {
  "apply" should {
    "return the same result when called twice" in {
      // mockito can't spy anonymous classes,
      // and this was the simplest approach i could come up with.
      class Adder extends (Int => Int) {
        override def apply(i: Int) = i + 1
      }

      val adder = spy(new Adder)
      val memoizer = Memoize.memoize(adder(_: Int))

      memoizer(1) should equal(2)
      memoizer(1) should equal(2)
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

      val adder = spy(new Fib)
      val memoizer = Memoize.memoize(adder(_: Int))

      memoizer(1) should equal(1)
      memoizer(1) should equal(1)
      memoizer(2) should equal(1)
      memoizer(2) should equal(1)
      memoizer(3) should equal(2)
      memoizer(3) should equal(2)

      verify(adder, times(1))(1)
      verify(adder, times(1))(2)
      verify(adder, times(1))(3)
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
      val memoizer = Memoize.memoize(fib(_: Int))

      memoizer(1) should equal(1)
      memoizer(1) should equal(1)
      memoizer(2) should equal(1)
      memoizer(2) should equal(1)
      memoizer(3) should equal(2)
      memoizer(3) should equal(2)

      verify(fib, times(1))(1)
      verify(fib, times(1))(2)
      verify(fib, times(1))(3)
    }

    "only runs the function once for the same input (adder)" in {
      // mockito can't spy anonymous classes,
      // and this was the simplest approach i could come up with.
      class Adder extends (Int => Int) {
        override def apply(i: Int) = i + 1
      }

      val adder = spy(new Adder)
      val memoizer = Memoize.memoize(adder(_: Int))

      memoizer(1) should equal(2)
      memoizer(1) should equal(2)
      memoizer(2) should equal(3)
      memoizer(2) should equal(3)

      verify(adder, times(1))(1)
      verify(adder, times(1))(2)
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
      val memoizer = Memoize.memoize(incrementer(_: Int))

      val ConcurrencyLevel = 5
      val computations =
        Future.collect(1 to ConcurrencyLevel map {
          _ =>
            FuturePool.unboundedPool(memoizer(5))
        })

      startUpLatch.countDown()
      val results = Await.result(computations)

      // All of the items are equal, up to reference equality
      results foreach {
        item =>
          val result = results(0)
          result should equal(item)
          result should be theSameInstanceAs item
      }

      // The effects happen exactly once
      callCount.get() should equal(1)
    }

    "handles exceptions during computations" in {
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
      val memoizer = Memoize.memoize(failFirstTime(_: Int))

      val ConcurrencyLevel = 5
      val computation =
        Future.collect(1 to ConcurrencyLevel map {
          _ =>
            FuturePool.unboundedPool(memoizer(5)) transform {
              Future.value
            }
        })

      startUpLatch.countDown()

      val (successes, failures) =
        Await.result(computation, 200.milliseconds).toList partition {
          _.isReturn
        }

      // One of the times, the computation must have failed.
      failures should equal(List(Throw(TheException)))

      // Another time, it must have succeeded, and then the stored
      // result will be reused for the other calls.
      successes should equal(List.fill(ConcurrencyLevel - 1)(Return(6)))

      // The exception plus another successful call:
      callCount.get() should equal(2)
    }
  }

  "bitset" should {
    val data = BitSet.empty + 3 + 4 + 4 + 100 + 101
    val jsonSerialiser = new JsonSerialiser

    "serialize to json" in {
      implicit val writesDPA = new Writes[BitSet] {
        def writes(data: BitSet): JsValue = Json.obj(
          "bitMask" -> data.toBitMask
        )
      }

      jsonSerialiser.serialize(data).toString should equal( """{"bitMask":[24,206158430208]}""")
    }
    "deserialize from json" in {
      implicit val bistsetJsonReader: Reads[BitSet] = (__ \ "bitMask").read[Array[Long]].map(BitSet.fromBitMask(_))


      val deserialized = jsonSerialiser.deserialize("""{"bitMask":[24,206158430208]}""")

      deserialized should equal(data)
    }

    "serialize to binary" in {
      object BinarySerialize {
        def write[A](o: A): Array[Byte] = {
          val ba = new java.io.ByteArrayOutputStream(512)
          val out = new java.io.ObjectOutputStream(ba)
          out.writeObject(o)
          out.close()
          ba.toByteArray()
        }

        def read[A](buffer: Array[Byte]): A = {
          val in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(buffer))
          in.readObject().asInstanceOf[A]
        }

        def check[A, B](x: A, y: B) = assert((x equals y) && (y equals x))
      }
      import BinarySerialize._
      import scala.collection.immutable.BitSet

      val data = BitSet.empty + 3 + 4 + 4 + 100 + 101

      val asBinary = write(data)
      val readFromBinary: BitSet = read(asBinary)
      check(data, readFromBinary)
    }
  }
}

