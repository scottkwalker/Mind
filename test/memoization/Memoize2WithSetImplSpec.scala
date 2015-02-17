package memoization

import composition.TestHelpers
import composition.TestComposition
import play.api.libs.json._
import serialization.JsonValidationException

final class Memoize2WithSetImplSpec extends TestHelpers {

  "apply" must {

    "return false when key is not in cache" in {
      val sut = new Memoize2WithSetImpl[Int, Int](versioning = "test") {
        override def size = ???
      }
      sut.add(Set.empty[String])
      sut.apply(0, 0) must equal(false)
    }

    "return true when key is in cache" in {
      val sut = new Memoize2WithSetImpl[Int, Int](versioning = "test") {
        override def size = ???
      }
      sut.add(Set("0|0"))
      sut.apply(0, 0) must equal(true)
    }
  }

  "write" must {
    "turn map into Json" in {
      val sut = new Memoize2WithSetImpl[Int, Int](versioning = "test") {
        override def size = ???
      }
      sut.add(Set("1|1", "1|2", "2|2"))

      sut(1, 1) must equal(true)
      sut(1, 2) must equal(true)
      sut(2, 2) must equal(true)

      sut.write must equal(
        JsObject(
          Seq(
            ("versioning", JsString("test")),
            ("cache",
              JsArray(
                value = Seq(
                  JsString("1|1"),
                  JsString("1|2"),
                  JsString("2|2")
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
          ("versioning", JsString("test")),
          ("cache",
            JsArray(
              value = Seq(
                JsString("1|1"),
                JsString("1|2"),
                JsString("2|2")
              )
            )
            )
        )
      )

      val asObj = Memoize2WithSetImpl.read[Dummy](json)

      asObj(0, 0) must equal(false)
      asObj(1, 1) must equal(true)
      asObj(1, 2) must equal(true)
      asObj(2, 2) must equal(true)
    }

    "throw when empty json" in {
      val json = JsObject(Seq.empty)

      a[JsonValidationException] must be thrownBy Memoize2WithSetImpl.read[Dummy](json)
    }
  }

  "size" must {
    "return 0 when empty" in {
      val memo = new Memoize2WithSetImpl[Int, Int] {}
      memo.size must equal(0)
    }

    "return 1 when only one entry" in {
      val memo = new Memoize2WithSetImpl[Int, Int] {}
      memo.add(Set("1|1"))

      memo.size must equal(1)
    }

    "return 3 when 3 entries" in {
      val memo = new Memoize2WithSetImpl[Int, Int] {}
      memo.add(Set("1|1", "2|2", "3|3"))
      memo.size must equal(3)
    }
  }

  class Dummy extends Memoize2WithSetImpl[Int, Int]() {}

  object Dummy {

    implicit val readJson: Reads[Dummy] =
      (__ \ "cache").read[Set[String]].map {
        setOfKeys =>
          val repo = new Dummy()
          repo.add(setOfKeys)
          repo
      }
  }

}