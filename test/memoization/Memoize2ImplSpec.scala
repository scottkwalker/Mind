package memoization

import java.util.concurrent.CountDownLatch

import composition.UnitTestHelpers
import org.mockito.Mockito._
import play.api.libs.json._
import serialization.JsonValidationException

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final class Memoize2ImplSpec extends UnitTestHelpers {

  "apply" must {
    "return the same result when called twice" in {
      val adder = new Memoize2Impl[Int, Int, Future[Int]] {
        override def funcCalculate(i: Int, j: Int): Future[Int] = Future.successful {
          i + j
        }

        override def size = ???
      }
      val a = adder(1, 1)
      val b = adder(1, 2)
      val c = adder(2, 2)

      val result = Future.sequence(Seq(a, b, c))

      whenReady(result) { r =>
        r(0) must equal(2)
        r(1) must equal(3)
        r(2) must equal(4)
      }(config = patienceConfig)
    }

    "only runs the function once for the same input (adder)" in {
      class Adder {

        def apply(i: Int, j: Int) = Future.successful {
          i + j
        }
      }

      val adder = spy(new Adder)
      val memoizer = new Memoize2Impl[Int, Int, Future[Int]] {
        override def funcCalculate(i: Int, j: Int): Future[Int] = adder(i, j)

        override def size = ???
      }
      val a1 = memoizer(1, 1)
      val a2 = memoizer(1, 1)
      val b1 = memoizer(1, 2)
      val b2 = memoizer(1, 2)
      val c1 = memoizer(1, 3)
      val c2 = memoizer(1, 3)
      val d1 = memoizer(2, 2)
      val d2 = memoizer(2, 2)

      val result = Future.sequence(Seq(a1, a2, b1, b2, c1, c2, d1, d2))

      whenReady(result) { r =>
        r(0) must equal(2)
        r(1) must equal(2)
        r(2) must equal(3)
        r(3) must equal(3)
        r(4) must equal(4)
        r(5) must equal(4)
        r(6) must equal(4)
        r(7) must equal(4)

        verify(adder, times(1))(1, 1)
        verify(adder, times(1))(1, 2)
        verify(adder, times(1))(1, 3)
        verify(adder, times(1))(2, 2)
        verifyNoMoreInteractions(adder)
      }(config = patienceConfig)
    }

    "only executes the memoized computation once per input" in {
      class Adder {

        def apply(i: Int, j: Int) = Future.successful {
          // Return a new object so that object equality will not pass
          // if two different result values are used.
          i + j
        }
      }

      val adder = spy(new Adder)
      val memoizer = new Memoize2Impl[Int, Int, Future[Int]] {
        override def funcCalculate(i: Int, j: Int): Future[Int] = adder(i, j)

        override def size = ???
      }

      whenReady(memoizer(5, 1)) { r =>
        verify(adder, times(1))(5, 1)
        verifyNoMoreInteractions(adder)
      }(config = patienceConfig)
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
      val memoizeThrowWhenCalled = new Memoize2Impl[Int, Int, Future[Int]] {
        override def funcCalculate(i: Int, j: Int): Future[Int] = throwWhenCalled(i, j)

        override def size = ???
      }

      a[RuntimeException] must be thrownBy memoizeThrowWhenCalled(5, 0).futureValue
    }
  }

  "write" must {
    "turn map into Json" in {
      val adder = new Memoize2Impl[Int, Int, Future[Int]](versioning = "test") {
        override def funcCalculate(i: Int, j: Int): Future[Int] = Future.successful {
          i + j
        }

        override def size = ???
      }
      val a = adder(1, 1)
      val b = adder(1, 2)
      val c = adder(2, 2)

      val result = Future.sequence(Seq(a, b, c))

      whenReady(result) { r =>
        r(0) must equal(2)
        r(1) must equal(3)
        r(2) must equal(4)

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
      }(config = patienceConfig)
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

      val asObj = Memoize2Impl.read[ThrowIfNotMemoizedWithFuture](json)

      val a = asObj(1, 1)
      val b = asObj(1, 2)
      val c = asObj(2, 2)
      val result = Future.sequence(Seq(a, b, c))

      whenReady(result) { r =>
        r(0) must equal(2)
        r(1) must equal(3)
        r(2) must equal(4)
      }(config = patienceConfig)
    }

    "throw when invalid json" in {
      val json = JsObject(Seq.empty)

      a[JsonValidationException] must be thrownBy Memoize2Impl.read[ThrowIfNotMemoizedWithFuture](json)
    }
  }

  "size" must {
    "return 0 when empty" in {
      val memo = new Memoize2Impl[Int, Int, Int] {
        override def funcCalculate(i: Int, j: Int): Int = i + j

        override def size = cache.size
      }
      memo.size must equal(0)
    }

    "return 1 when only one entry" in {
      val memo = new Memoize2Impl[Int, Int, Int] {
        override def funcCalculate(i: Int, j: Int): Int = i + j

        override def size = cache.size
      }

      memo(1, 1)

      memo.size must equal(1)
    }

    "return 3 when 3 entries" in {
      val adder = new Memoize2Impl[Int, Int, Int] {
        override def funcCalculate(i: Int, j: Int): Int = i + j

        override def size = cache.size
      }

      adder(1, 1)
      adder(2, 2)
      adder(3, 3)

      adder.size must equal(3)
    }
  }

  private implicit val mapOfStringToInt = new Writes[Map[String, Either[CountDownLatch, Int]]] {
    def writes(cache: Map[String, Either[CountDownLatch, Int]]): JsValue = {
      val computedKeyValues = cache.flatMap {
        case (k, Right(v)) =>
          Some(k -> v) // Only store the computed values (the 'right-side').
        case _ => None
      }
      Json.toJson(computedKeyValues)
    }
  }

  private implicit val mapOfStringToFutureInt = new Writes[Map[String, Either[CountDownLatch, Future[Int]]]] {
    def writes(cache: Map[String, Either[CountDownLatch, Future[Int]]]): JsValue = {
      val computedKeyValues = cache.flatMap {
        case (k, Right(v)) if v.isCompleted =>
          val computed = Await.result(v, finiteTimeout) // It should be OK to use blocking Await here are the result is already computed so should instantly be returned.
          Some(k -> computed) // Only store the computed values (the 'right-side').
        case _ => None
      }
      Json.toJson(computedKeyValues)
    }
  }

  class ThrowIfNotMemoized() extends Memoize2Impl[Int, Int, Int]() {

    override def funcCalculate(key: Int, key2: Int): Int = throw new Exception("funcCalculate must not be called as the result must have been retrieved from the json")

    def replaceCache(newCache: Map[String, Either[CountDownLatch, Int]]) = cache = newCache

    override def size: Int = ???
  }

  class ThrowIfNotMemoizedWithFuture() extends Memoize2Impl[Int, Int, Future[Int]]() {

    override def funcCalculate(key: Int, key2: Int): Future[Int] = throw new Exception("funcCalculate must not be called as the result must have been retrieved from the json")

    def replaceCache(newCache: Map[String, Either[CountDownLatch, Future[Int]]]) = cache = newCache

    override def size: Int = ???
  }

  object ThrowIfNotMemoizedWithFuture {

    implicit val readJson: Reads[ThrowIfNotMemoized] =
      (__ \ "cache").read[Map[String, Int]].map {
        keyValueMap =>
          val cache = keyValueMap.map {
            case (k, v) => k -> Right[CountDownLatch, Int](v)
          }
          val memo = new ThrowIfNotMemoized()
          memo.replaceCache(cache)
          memo
      }

    implicit val readJsonWithFutures: Reads[ThrowIfNotMemoizedWithFuture] =
      (__ \ "cache").read[Map[String, Int]].map {
        keyValueMap =>
          val cache = keyValueMap.map {
            case (k, v) => k -> Right[CountDownLatch, Future[Int]](Future.successful(v))
          }
          val memo = new ThrowIfNotMemoizedWithFuture()
          memo.replaceCache(cache)
          memo
      }
  }

}