package models.common

import java.util.concurrent.CountDownLatch

//import com.fasterxml.jackson.annotation.JsonFormat

import composition.UnitTestHelpers
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.Json.obj
import play.api.libs.json._
import serialization.JsonDeserialiser

//import spray.json._

import models.common.Scope.Form._

import scala.collection.immutable.BitSet

final class ScopeSpec extends UnitTestHelpers {

  "constructor" must {
    "set default values to zero" in {
      Scope() match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(0)
          numFuncs must equal(0)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }

    "throw if height > maxHeight" in {
      a[RuntimeException] must be thrownBy Scope(height = 1, maxHeight = 0)
    }
  }

  "incrementVals" must {
    "return expected" in {
      Scope().incrementVals match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(1)
          numFuncs must equal(0)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "incrementFuncs" must {
    "return expected" in {
      Scope().incrementFuncs match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(0)
          numFuncs must equal(1)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "incrementObjects" must {
    "return expected" in {
      Scope().incrementObjects match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(0)
          numFuncs must equal(0)
          numObjects must equal(1)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "decrementHeight" must {
    "return expected" in {
      Scope(height = 2, maxHeight = 10).decrementHeight match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(0)
          numFuncs must equal(0)
          numObjects must equal(0)
          height must equal(1)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(10)
        case _ => fail("must have matched")
      }
    }
  }

  "fluent interface" must {
    "return expected" in {
      Scope(height = 2, maxHeight = 10).
        incrementVals.
        incrementFuncs.
        incrementObjects.
        decrementHeight match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(1)
          numFuncs must equal(1)
          numObjects must equal(1)
          height must equal(1)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(10)
        case _ => fail("must have matched")
      }
    }
  }

  "setNumFuncs" must {
    "return expected" in {
      Scope().setNumFuncs(1) match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(0)
          numFuncs must equal(1)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "setNumVals" must {
    "return expected" in {
      Scope().setNumVals(1) match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree, maxHeight) =>
          numVals must equal(1)
          numFuncs must equal(0)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
          maxHeight must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "serialize" must {
    "return expected json" in {
      Json.toJson(asModel) must equal(asJson)
    }
  }

  "deserialize" must {
    "return expected mode" in {
      JsonDeserialiser.deserialize[Scope](asJsonString) must equal(asModel)
    }
  }

  "experiment with json serialization" must {
    "Seq[Int]" in {
      Json.toJson(Seq[Int](0, 1, 2)) must equal(JsArray(Seq(JsNumber(0), JsNumber(1), JsNumber(2))))
    }

    "Either[String, Int]" in {
      implicit val jsonWrites = new Writes[Either[String, Int]] {
        def writes(o: Either[String, Int]): JsValue = obj(
          o.fold(
            stringContent => "stringContent" -> JsString(stringContent),
            intContent => "intContent" -> JsNumber(intContent)
          )
        )
      }

      Json.toJson(Left("test")) must equal(JsObject(Seq(("stringContent", JsString("test")))))
      Json.toJson(Right(123)) must equal(JsObject(Seq(("intContent", JsNumber(123)))))
    }

    "Either[IScope, Int]" in {
      implicit val jsonWrites = new Writes[Either[IScope, Int]] {
        def writes(o: Either[IScope, Int]): JsValue = obj(
          o.fold(
            scopeContent => "scopeContent" -> Json.toJson(scopeContent),
            intContent => "intContent" -> JsNumber(intContent)
          )
        )
      }

      Json.toJson(Left(asModel)) must equal(asJsonWithType)
      Json.toJson(Right(123)) must equal(JsObject(Seq(("intContent", JsNumber(123)))))
    }

    "Either[IScope, Seq[Int]]" in {
      implicit val jsonWrites = new Writes[Either[IScope, Seq[Int]]] {
        def writes(o: Either[IScope, Seq[Int]]): JsValue = obj(
          o.fold(
            scopeContent => "scopeContent" -> Json.toJson(scopeContent),
            intContent => "intContent" -> Json.toJson(intContent)
          )
        )
      }

      Json.toJson(Left(asModel)) must equal(asJsonWithType)
      Json.toJson(Right(Seq[Int](0, 1, 2))) must equal(
        JsObject(
          Seq(
            ("intContent", JsArray(Seq(JsNumber(0), JsNumber(1), JsNumber(2))))
          )
        )
      )
    }

    "Map[String, Int]" in {
      implicit val jsonWrites = new Writes[Map[String, Int]] {
        def writes(o: Map[String, Int]): JsValue = Json.toJson(o)
      }

      Json.toJson(Map[String, Int]("a" -> 123)) must equal(JsObject(Seq(
        ("a", JsNumber(123))
      )))
    }

    "Map[Int, Int]" in {
      implicit val jsonWrites = new Writes[Map[Int, Int]] {
        def writes(o: Map[Int, Int]): JsValue = {
          val keyAsString = o.map { kv => kv._1.toString -> kv._2} // Convert to Map[String,Int] which it can convert
          Json.toJson(keyAsString)
        }
      }

      Json.toJson(Map[Int, Int](0 -> 123)) must equal(
        JsObject(
          Seq(
            ("0", JsNumber(123))
          )
        )
      )
    }

    "Map[IScope, Int]" in {
      implicit val jsonWrites = new Writes[Map[IScope, Int]] {
        def writes(o: Map[IScope, Int]): JsValue = {
          val keyAsString = o.map { kv => kv._1.toString -> kv._2} // Convert to Map[String,Int] which it can convert
          Json.toJson(keyAsString)
        }
      }

      Json.toJson(Map[IScope, Int](Scope() -> 123)) must equal(
        JsObject(
          Seq(
            ("Scope(0,0,0,0,0,0,0,0,0)", JsNumber(123))
          )
        )
      )
    }

    "Map[Int, BitSet]" in {
      implicit val jsonWrites = new Writes[Map[Int, BitSet]] {
        def writes(o: Map[Int, BitSet]): JsValue = {
          val keyAsString = o.map { kv => kv._1.toString -> kv._2.toBitMask.mkString(".")} // Convert to Map[String,Int] which it can convert
          Json.toJson(keyAsString)
        }
      }

      Json.toJson(Map[Int, BitSet](1 -> (BitSet.empty + 1 + 2))) must equal(
        JsObject(
          Seq(
            ("1", JsString("6"))
          )
        )
      )
    }

    "Map[String, Either[IScope, Seq[Int]]]" in {
      implicit val jsonWrites = new Writes[Either[IScope, Seq[Int]]] {
        def writes(o: Either[IScope, Seq[Int]]): JsValue = obj(
          o.fold(
            scopeContent => "scopeContent" -> Json.toJson(scopeContent),
            intContent => "intContent" -> Json.toJson(intContent)
          )
        )
      }
      implicit val jsonWrites2 = new Writes[Map[String, Either[IScope, Seq[Int]]]] {
        def writes(o: Map[String, Either[IScope, Seq[Int]]]): JsValue = Json.toJson(o)
      }

      Json.toJson(Map("key" -> Left(asModel))) must equal(
        JsObject(
          Seq(
            ("key", asJsonWithType)
          )
        )
      )

      Json.toJson(Map("key" -> Right(Seq[Int](0, 1, 2)))) must equal(
        JsObject(
          Seq(
            ("key", JsObject(
              Seq(
                ("intContent", JsArray(Seq(JsNumber(0), JsNumber(1), JsNumber(2))))
              )
            )
              )
          )
        )
      )
    }

    "Map[String, Either[CountDownLatch, Int]]" in {
      implicit val jsonWrites = new Writes[Either[CountDownLatch, Int]] {
        def writes(o: Either[CountDownLatch, Int]): JsValue = obj(
          o.fold(
            countDownLatchContent => ???,
            intContent => "intContent" -> Json.toJson(intContent)
          )
        )
      }
      implicit val jsonWrites2 = new Writes[Map[String, Either[CountDownLatch, Int]]] {
        def writes(o: Map[String, Either[CountDownLatch, Int]]): JsValue = Json.toJson(o.filter {
          x => x._2.isRight // Only completed values.
        })
      }
      val countdownLatchModel = new CountDownLatch(1)

      Json.toJson(Map("keyLeft" -> Left(countdownLatchModel))) must equal(JsObject(Seq.empty))

      Json.toJson(Map("keyRight" -> Right(1))) must equal(
        JsObject(
          Seq(
            ("keyRight",
              JsObject(
                Seq(
                  ("intContent", JsNumber(1))
                )
              )
              )
          )
        )
      )

      Json.toJson(Map(
        "keyLeft" -> Left(countdownLatchModel),
        "keyRight" -> Right(1)
      )
      ) must equal(
        JsObject(
          Seq(
            ("keyRight",
              JsObject(
                Seq(
                  ("intContent", JsNumber(1))
                )
              )
              )
          )
        )
      )
    }

    "Map[String, Either[CountDownLatch, Seq[Int]]]" in {
      implicit val jsonWrites = new Writes[Either[CountDownLatch, Seq[Int]]] {
        def writes(o: Either[CountDownLatch, Seq[Int]]): JsValue = obj(
          o.fold(
            countDownLatchContent => ???,
            intContent => "intContent" -> Json.toJson(intContent)
          )
        )
      }
      implicit val jsonWrites2 = new Writes[Map[String, Either[CountDownLatch, Seq[Int]]]] {
        def writes(o: Map[String, Either[CountDownLatch, Seq[Int]]]): JsValue = Json.toJson(o.filter {
          x => x._2.isRight // Only completed values.
        })
      }
      val countdownLatchModel = new CountDownLatch(1)

      Json.toJson(Map("keyLeft" -> Left(countdownLatchModel))) must equal(JsObject(Seq.empty))

      Json.toJson(Map("keyRight" -> Right(Seq[Int](0, 1, 2)))) must equal(
        JsObject(
          Seq(
            ("keyRight",
              JsObject(
                Seq(
                  ("intContent", JsArray(Seq(JsNumber(0), JsNumber(1), JsNumber(2))))
                )
              )
              )
          )
        )
      )

      Json.toJson(Map(
        "keyLeft" -> Left(countdownLatchModel),
        "keyRight" -> Right(Seq[Int](0, 1, 2))
      )
      ) must equal(
        JsObject(
          Seq(
            ("keyRight",
              JsObject(
                Seq(
                  ("intContent", JsArray(Seq(JsNumber(0), JsNumber(1), JsNumber(2))))
                )
              )
              )
          )
        )
      )
    }

    "Map[Int, Either[CountDownLatch, Seq[Int]]]" in {
      implicit val jsonWrites = new Writes[Either[CountDownLatch, Seq[Int]]] {
        def writes(o: Either[CountDownLatch, Seq[Int]]): JsValue = obj(
          o.fold(
            countDownLatchContent => ???,
            intContent => "intContent" -> Json.toJson(intContent)
          )
        )
      }
      implicit val jsonWrites2 = new Writes[Map[Int, Either[CountDownLatch, Seq[Int]]]] {
        def writes(o: Map[Int, Either[CountDownLatch, Seq[Int]]]): JsValue = {
          val filtered = o.
            filter(kvp => kvp._2.isRight). // Only completed values.
            map(kvp => kvp._1.toString -> kvp._2) // Key must be string

          Json.toJson(filtered)
        }
      }
      val countdownLatchModel = new CountDownLatch(1)

      Json.toJson(Map(1 -> Left(countdownLatchModel))) must equal(JsObject(Seq.empty))

      Json.toJson(Map(2 -> Right(Seq[Int](0, 1, 2)))) must equal(
        JsObject(
          Seq(
            ("2",
              JsObject(
                Seq(
                  ("intContent", JsArray(Seq(JsNumber(0), JsNumber(1), JsNumber(2))))
                )
              )
              )
          )
        )
      )

      Json.toJson(Map(
        1 -> Left(countdownLatchModel),
        2 -> Right(Seq[Int](0, 1, 2))
      )
      ) must equal(
        JsObject(
          Seq(
            ("2",
              JsObject(
                Seq(
                  ("intContent", JsArray(Seq(JsNumber(0), JsNumber(1), JsNumber(2))))
                )
              )
              )
          )
        )
      )
    }

    "Map[IScope, Either[CountDownLatch, Seq[Int]]]" in {
      implicit val jsonWrites = new Writes[Either[CountDownLatch, Seq[Int]]] {
        def writes(o: Either[CountDownLatch, Seq[Int]]): JsValue = obj(
          o.fold(
            countDownLatchContent => ???,
            intContent => "intContent" -> Json.toJson(intContent)
          )
        )
      }
      implicit val jsonWrites2 = new Writes[Map[IScope, Either[CountDownLatch, Seq[Int]]]] {
        def writes(o: Map[IScope, Either[CountDownLatch, Seq[Int]]]): JsValue = {
          val filtered = o.
            filter(kv => kv._2.isRight). // Only completed values. // TODO is there an in-place filter to avoid writing new map.
            map(kv => kv._1.toString -> kv._2) // Json keys must be strings.
          Json.toJson(filtered)
        }
      }
      val countdownLatchModel = new CountDownLatch(1)
      val left = Left(countdownLatchModel)
      val right = Right(Seq[Int](0, 1, 2))
      val expected = JsObject(
        Seq(
          ("Scope(0,0,0,1,0,0,0,0,1)",
            JsObject(
              Seq(
                ("intContent", JsArray(Seq(JsNumber(0), JsNumber(1), JsNumber(2))))
              )
            )
            )
        )
      )

      Json.toJson(Map[IScope, Either[CountDownLatch, Seq[Int]]](Scope() -> left)) must equal(JsObject(Seq.empty))

      Json.toJson(Map[IScope, Either[CountDownLatch, Seq[Int]]](Scope(height = 1, maxHeight = 1) -> right)) must equal(expected)

      Json.toJson(Map[IScope, Either[CountDownLatch, Seq[Int]]](
        Scope() -> left,
        Scope(height = 1, maxHeight = 1) -> right
      )
      ) must equal(expected)
    }

    "Seq[(String, Any)]" in {
      implicit val jsonWrites = new Writes[Seq[(String, Any)]] {
        def writes(o: Seq[(String, Any)]): JsValue = obj(
          o.map {
            case (key: String, value: Any) =>
              val ret: (String, JsValueWrapper) = value match {
                case asString: String => key -> JsString(asString)
                case asInt: Int => key -> JsNumber(asInt)
                case asDouble: Double => key -> JsNumber(asDouble)
                case None => key -> JsNull
                case asBool: Boolean => key -> JsBoolean(asBool)
                case _ => throw new RuntimeException("no match, you need to tell it how to cast this type to json")
              }
              ret
          }.toSeq: _*
        )
      }
      val key0 = "key0"
      val key1 = "key1"
      val key2 = "key2"
      val key3 = "key3"
      val key4 = "key4"
      val expectedString = "test-field-0"
      val expectedInt = 123
      val expectedDouble: Double = 2.0
      val expectedOption: Option[Int] = None
      val expectedBool = true
      val data: Seq[(String, Any)] = Seq((key0, expectedString), (key1, expectedInt), (key2, expectedDouble), (key3, expectedOption), (key4, expectedBool))
      val asJson = Json.toJson(data)
      asJson must equal(JsObject(
        Seq(
          (key0, JsString(expectedString)),
          (key1, JsNumber(expectedInt)),
          (key2, JsNumber(expectedDouble)),
          (key3, JsNull),
          (key4, JsBoolean(expectedBool))
        )
      ))
      Json.stringify(asJson) must equal( s"""{"$key0":"$expectedString","$key1":$expectedInt,"$key2":$expectedDouble,"$key3":null,"$key4":true}""")
    }
  }

  private val asJson = JsObject(
    fields = Seq(
      ("numVals", JsNumber(1)),
      ("numFuncs", JsNumber(2)),
      ("numObjects", JsNumber(3)),
      ("height", JsNumber(4)),
      ("maxExpressionsInFunc", JsNumber(5)),
      ("maxFuncsInObject", JsNumber(6)),
      ("maxParamsInFunc", JsNumber(7)),
      ("maxObjectsInTree", JsNumber(8)),
      (MaxHeightId, JsNumber(9))
    )
  )
  private val asJsonString = Json.stringify(asJson)
  private val asModel: IScope = Scope(
    numVals = 1,
    numFuncs = 2,
    numObjects = 3,
    height = 4,
    maxExpressionsInFunc = 5,
    maxFuncsInObject = 6,
    maxParamsInFunc = 7,
    maxObjectsInTree = 8,
    maxHeight = 9
  )
  private val asJsonWithType = JsObject(
    Seq(
      ("scopeContent", asJson)
    )
  )
}