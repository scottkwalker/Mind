package memoization

import java.util.concurrent.CountDownLatch
import composition.TestComposition
import org.mockito.Mockito._
import play.api.libs.json.Json.obj
import play.api.libs.json._
import serialization.JsonValidationException
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

final class Memoize2ImplSpec extends TestComposition {

  "apply" must {
    "return the same result when called twice" in {
      val adder = new Memoize2Impl[Int, Int, Int] {
        override def f(i: Int, j: Int): Future[Int] = Future.successful {
          i + j
        }
      }

      Await.result(adder(1, 1), finiteTimeout) must equal(2)
      Await.result(adder(1, 2), finiteTimeout) must equal(3)
      Await.result(adder(2, 2), finiteTimeout) must equal(4)
    }

    "only runs the function once for the same input (adder)" in {
      class Adder {

        def apply(i: Int, j: Int) = Future.successful {
          i + j
        }
      }

      val adder = spy(new Adder)
      val memoizer = new Memoize2Impl[Int, Int, Int] {
        override def f(i: Int, j: Int): Future[Int] = adder(i, j)
      }

      Await.result(memoizer(1, 1), finiteTimeout) must equal(2)
      Await.result(memoizer(1, 1), finiteTimeout) must equal(2)
      Await.result(memoizer(1, 2), finiteTimeout) must equal(3)
      Await.result(memoizer(1, 2), finiteTimeout) must equal(3)
      Await.result(memoizer(1, 3), finiteTimeout) must equal(4)
      Await.result(memoizer(1, 3), finiteTimeout) must equal(4)
      Await.result(memoizer(2, 2), finiteTimeout) must equal(4)
      Await.result(memoizer(2, 2), finiteTimeout) must equal(4)

      verify(adder, times(1))(1, 1)
      verify(adder, times(1))(1, 2)
      verify(adder, times(1))(2, 2)
    }

    "only executes the memoized computation once per input" in {
      implicit val eitherLatchOrStringToJson = new Writes[Either[CountDownLatch, Future[String]]] {
        def writes(o: Either[CountDownLatch, Future[String]]): JsValue = obj(
          o.fold(
            countDownLatchContent => ???, // must be filtered out at a higher level so that we do not store incomplete calculations.
            right => {
              val computed = scala.concurrent.Await.result(right, Duration.Inf)
              stateKey -> Json.toJson(computed)
            }
          )
        )
      }

      class Adder {

        def apply(i: Int, j: Int) = Future.successful {
          // Return a new object so that object equality will not pass
          // if two different result values are used.
          i + j
        }
      }

      val adder = spy(new Adder)
      val memoizer = new Memoize2Impl[Int, Int, Int] {
        override def f(i: Int, j: Int): Future[Int] = adder(i, j)
      }

      Await.result(memoizer(5, 1), finiteTimeout)

      verify(adder, times(1))(5, 1)
    }

    "handles exceptions during computations" in {
      class ThrowWhenCalled {

        def apply(i: Int, j: Int) = {
          Future.failed(throw new RuntimeException)
        }
      }

      // A computation that must fail the first time, and then
      // succeed for all subsequent attempts.
      val throwWhenCalled = spy(new ThrowWhenCalled)
      val memoizeThrowWhenCalled = new Memoize2Impl[Int, Int, Int] {
        override def f(i: Int, j: Int): Future[Int] = throwWhenCalled(i, j)
      }

      a[RuntimeException] must be thrownBy Await.result(memoizeThrowWhenCalled(5, 0), finiteTimeout)
    }
  }

  "write" must {
    "turn map into Json" in {
      val adder = new Memoize2Impl[Int, Int, Int](versioning = "test") {
        override def f(i: Int, j: Int): Future[Int] = Future.successful {
          i + j
        }
      }
      Await.result(adder(1, 1), finiteTimeout) must equal(2)
      Await.result(adder(1, 2), finiteTimeout) must equal(3)
      Await.result(adder(2, 2), finiteTimeout) must equal(4)

      adder.write must equal(
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

      Await.result(asObj(1, 1), finiteTimeout) must equal(2)
      Await.result(asObj(1, 2), finiteTimeout) must equal(3)
      Await.result(asObj(2, 2), finiteTimeout) must equal(4)
    }

    "throw when invalid json" in {
      val json = JsObject(Seq.empty)

      a[JsonValidationException] must be thrownBy Memoize2Impl.read[ThrowIfNotMemoized](json)
    }
  }

  private val stateKey = "neighbours"

  private implicit val mapOfStringToInt = new Writes[Map[String, Either[CountDownLatch, Future[Int]]]] {
    def writes(cache: Map[String, Either[CountDownLatch, Future[Int]]]): JsValue = {
      val computedKeyValues = cache.flatMap {
        case (k, Right(v)) if v.isCompleted =>
          val computed = scala.concurrent.Await.result(v, Duration.Inf)
          Some(k -> computed) // Only store the computed values (the 'right-side').
        case _ => None
      }
      Json.toJson(computedKeyValues)
    }
  }

  class ThrowIfNotMemoized() extends Memoize2Impl[Int, Int, Int]() {

    override def f(key: Int, key2: Int): Future[Int] = throw new Exception("fAsync must not be called as the result must have been retrieved from the json")

    def replaceCache(newCache: Map[String, Either[CountDownLatch, Future[Int]]]) = cache = newCache
  }

  object ThrowIfNotMemoized {

    implicit val readJson: Reads[ThrowIfNotMemoized] =
      (__ \ "cache").read[Map[String, Int]].map {
        keyValueMap =>
          val cache = keyValueMap.map {
            case (k, v) => k -> Right[CountDownLatch, Future[Int]](Future.successful(v))
          }
          val memo = new ThrowIfNotMemoized()
          memo.replaceCache(cache)
          memo
      }
  }

}