package nodes.memoization

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{CountDownLatch, TimeUnit}
import com.twitter.conversions.time._
import com.twitter.util._
import models.common.JsonValidationException
import org.mockito.Mockito._
import play.api.libs.json.Json.obj
import play.api.libs.json._
import composition.TestComposition

final class Memoize2ImplSpec extends TestComposition {

  "apply" must {
    "return the same result when called twice" in {
      val memoizeAddTogether = new Memoize2Impl[Int, Int, Int] {
        def f(i: Int, j: Int): Int = i + j
      }

      memoizeAddTogether(1, 1) must equal(2)
      memoizeAddTogether(1, 2) must equal(3)
      memoizeAddTogether(2, 2) must equal(4)
    }

    "only runs the function once for the same input (adder)" in {
      class F {

        def apply(i: Int, j: Int): Int = invoke(i, j)

        private def invoke(i: Int, j: Int) = i + j
      }

      val adder = spy(new F)
      val memoizer = new Memoize2Impl[Int, Int, Int] {
        def f(i: Int, j: Int): Int = adder(i, j)
      }

      memoizer(1, 1) must equal(2)
      memoizer(1, 1) must equal(2)
      memoizer(1, 2) must equal(3)
      memoizer(1, 2) must equal(3)
      memoizer(1, 3) must equal(4)
      memoizer(1, 3) must equal(4)
      memoizer(2, 2) must equal(4)
      memoizer(2, 2) must equal(4)

      verify(adder, times(1))(1, 1)
      verify(adder, times(1))(1, 2)
      verify(adder, times(1))(2, 2)
    }

    "only executes the memoized computation once per input" in {
      implicit val eitherLatchOrStringToJson = new Writes[Either[CountDownLatch, String]] {
        def writes(o: Either[CountDownLatch, String]): JsValue = obj(
          o.fold(
            countDownLatchContent => ???, // must be filtered out at a higher level so that we do not store incomplete calculations.
            right => stateKey -> Json.toJson(right)
          )
        )
      }

      val callCount = new AtomicInteger(0)
      val startUpLatch = new CountDownLatch(1)

      class Incrementer {

        def apply(i: Int, j: Int) = {
          // Wait for all of the threads to be started before
          // continuing. This gives races a chance to happen.
          startUpLatch.await()

          // Perform the effect of incrementing the counter, so that we
          // can detect whether this code is executed more than once.
          callCount.incrementAndGet()

          // Return a new object so that object equality will not pass
          // if two different result values are used.
          "." * i * j
        }
      }

      val incrementer = spy(new Incrementer)
      val memoizeIncrementer = new Memoize2Impl[Int, Int, String] {
        def f(i: Int, j: Int): String = incrementer(i, j)
      }

      val ConcurrencyLevel = 5
      val computations =
        Future.collect(1 to ConcurrencyLevel map {
          _ =>
            FuturePool.unboundedPool(memoizeIncrementer(5, 1))
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

      class FailFirstTime {

        def apply(i: Int, j: Int) = {
          // Ensure that all of the callers have been started
          startUpLatch.await(200, TimeUnit.MILLISECONDS)
          // This effect must happen once per exception plus once for
          // all successes
          val n = callCount.incrementAndGet()
          if (n == 1) throw TheException else i + j + 1
        }
      }

      // A computation that must fail the first time, and then
      // succeed for all subsequent attempts.
      val failFirstTime = spy(new FailFirstTime)
      val memoizeFailFirstTime = new Memoize2Impl[Int, Int, Int] {
        def f(i: Int, j: Int): Int = failFirstTime(i, j)
      }

      val ConcurrencyLevel = 5
      val computation =
        Future.collect(1 to ConcurrencyLevel map {
          _ =>
            FuturePool.unboundedPool(memoizeFailFirstTime(5, 0)) transform {
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
      val memoizeAddTogether = new Memoize2Impl[Int, Int, Int](versioning = "test") {
        def f(i: Int, j: Int): Int = i + j
      }
      //{"versioning":"test","cache":{"cache":{"1|1":2,"1|2":3,"2|2":4}}}
      //{"versioning":"test","cache":{"1|1":2,"1|2":3,"2|2":4}}
      memoizeAddTogether(1, 1) must equal(2)
      memoizeAddTogether(1, 2) must equal(3)
      memoizeAddTogether(2, 2) must equal(4)

      memoizeAddTogether.write must equal(
        JsObject(
          Seq(
            ("versioning", JsString("test")),
            ("cache",
              JsObject(
                Seq(
                  ("1|1", JsNumber(2)),
                  ("1|2", JsNumber(3)),
                  ("2|2", JsNumber(4))
                )
              )
              )
          )
        )
      )
    }
  }

  "read" must {
    "turn json to usable object" in {
      val json = JsObject(
        Seq(
          ("cache",
            JsObject(
              Seq(
                ("1|1", JsNumber(2)),
                ("1|2", JsNumber(3)),
                ("2|2", JsNumber(4))
              )
            )
            )
        )
      )

      val asObj = Memoize2Impl.read[ThrowIfNotMemoized](json)

      asObj(1, 1) must equal(2)
      asObj(1, 2) must equal(3)
      asObj(2, 2) must equal(4)
    }

    "throw when invalid json" in {
      val json = JsObject(Seq.empty)

      a[JsonValidationException] must be thrownBy Memoize2Impl.read[ThrowIfNotMemoized](json)
    }
  }

  private final val stateKey = "neighbours"

  private implicit val mapOfStringToInt = new Writes[Map[String, Either[CountDownLatch, Int]]] {
    def writes(cache: Map[String, Either[CountDownLatch, Int]]): JsValue = {
      val computedKeyValues = cache.flatMap {
        case (k, Right(v)) => Some(k -> v) // Only store the computed values (the 'right-side').
        case _ => None
      }
      Json.toJson(computedKeyValues)
    }
  }

  private class ThrowIfNotMemoized(private var cache: Map[String, Either[CountDownLatch, Int]]) extends Memoize2Impl[Int, Int, Int](cache) {

    def f(i: Int, j: Int): Int = throw new Exception("must not be called as the result must have been retrieved from the json")
  }

  private implicit val adderFromJson: Reads[ThrowIfNotMemoized] =
    (__ \ "cache").read[Map[String, Int]].map {
      keyValueMap =>
        val cache = keyValueMap.map {
          case (k, v) => k -> Right[CountDownLatch, Int](v)
        }
        new ThrowIfNotMemoized(cache)
    }
}