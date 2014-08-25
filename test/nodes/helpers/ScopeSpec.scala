package nodes.helpers

import java.util.concurrent.CountDownLatch
import com.google.inject.Injector
import models.common.{IScope, Scope}
import modules.ai.aco.AcoModule
import play.api.libs.json.Json.obj
import play.api.libs.json._
import composition.TestComposition
import scala.collection.immutable.BitSet

final class ScopeSpec extends TestComposition {

  "constructor" must {
    "set default values to zero" in {
      Scope() match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree) =>
          numVals must equal(0)
          numFuncs must equal(0)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "incrementVals" must {
    "return expected" in {
      Scope().incrementVals match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree) =>
          numVals must equal(1)
          numFuncs must equal(0)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "incrementFuncs" must {
    "return expected" in {
      Scope().incrementFuncs match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree) =>
          numVals must equal(0)
          numFuncs must equal(1)
          numObjects must equal(0)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "incrementObjects" must {
    "return expected" in {
      Scope().incrementObjects match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree) =>
          numVals must equal(0)
          numFuncs must equal(0)
          numObjects must equal(1)
          height must equal(0)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "decrementHeight" must {
    "return expected" in {
      Scope(height = 2).decrementHeight match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree) =>
          numVals must equal(0)
          numFuncs must equal(0)
          numObjects must equal(0)
          height must equal(1)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "fluent interface" must {
    "return expected" in {
      Scope(height = 2).
        incrementVals.
        incrementFuncs.
        incrementObjects.
        decrementHeight match {
        case Scope(numVals, numFuncs, numObjects, height, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxObjectsInTree) =>
          numVals must equal(1)
          numFuncs must equal(1)
          numObjects must equal(1)
          height must equal(1)
          maxExpressionsInFunc must equal(0)
          maxFuncs must equal(0)
          maxParamsInFunc must equal(0)
          maxObjectsInTree must equal(0)
        case _ => fail("must have matched")
      }
    }
  }

  "IoC create" must {
    "return a new instance with injected values" in {
      val injector: Injector = testInjector(new AcoModule)
      val sut = injector.getInstance(classOf[IScope])

      sut.maxFuncsInObject must equal(10)
      sut.maxExpressionsInFunc must equal(2)
    }
  }

  "serialize" must {
    "return expected json" in {
      Json.toJson(scopeAsModel) must equal(JsObject(Seq(
        ("numVals", JsNumber(0)),
        ("numFuncs", JsNumber(0)),
        ("numObjects", JsNumber(0)),
        ("height", JsNumber(0)),
        ("maxExpressionsInFunc", JsNumber(0)),
        ("maxFuncsInObject", JsNumber(0)),
        ("maxParamsInFunc", JsNumber(0)),
        ("height", JsNumber(0)),
        ("maxObjectsInTree", JsNumber(0))
      )))
    }
  }

  "deserialize" must {
    "return expected mode" in {
      JsonDeserialiser.deserialize[Scope](scopeAsJsonString) must equal(scopeAsModel)
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

      Json.toJson(Left(scopeAsModel)) must equal(
        JsObject(
          Seq(
            ("scopeContent",
              JsObject(
                Seq(
                  ("numVals", JsNumber(0)),
                  ("numFuncs", JsNumber(0)),
                  ("numObjects", JsNumber(0)),
                  ("height", JsNumber(0)),
                  ("maxExpressionsInFunc", JsNumber(0)),
                  ("maxFuncsInObject", JsNumber(0)),
                  ("maxParamsInFunc", JsNumber(0)),
                  ("height", JsNumber(0)),
                  ("maxObjectsInTree", JsNumber(0))
                )
              )
              )
          )
        )
      )
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

      Json.toJson(Left(scopeAsModel)) must equal(
        JsObject(
          Seq(
            ("scopeContent",
              JsObject(// Objects are the mapping type in JSON.
                Seq(
                  ("numVals", JsNumber(0)),
                  ("numFuncs", JsNumber(0)),
                  ("numObjects", JsNumber(0)),
                  ("height", JsNumber(0)),
                  ("maxExpressionsInFunc", JsNumber(0)),
                  ("maxFuncsInObject", JsNumber(0)),
                  ("maxParamsInFunc", JsNumber(0)),
                  ("height", JsNumber(0)),
                  ("maxObjectsInTree", JsNumber(0))
                )
              )
              )
          )
        )
      )
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

      Json.toJson(Map[String, Int]("a" -> 1)) must equal(JsObject(Seq(
        ("a", JsNumber(1))
      )))
    }

    "Map[Int, Int]" in {
      implicit val jsonWrites = new Writes[Map[Int, Int]] {
        def writes(o: Map[Int, Int]): JsValue = {
          val keyAsString = o.map { kv => kv._1.toString -> kv._2} // Convert to Map[String,Int] which it can convert
          Json.toJson(keyAsString)
        }
      }

      Json.toJson(Map[Int, Int](0 -> 1)) must equal(
        JsObject(
          Seq(
            ("0", JsNumber(1))
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

      Json.toJson(Map[IScope, Int](Scope() -> 1)) must equal(
        JsObject(
          Seq(
            ("Scope(0,0,0,0,0,0,0,0)", JsNumber(1))
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

      Json.toJson(Map("key" -> Left(scopeAsModel))) must equal(
        JsObject(
          Seq(
            ("key",
              JsObject(
                Seq(
                  ("scopeContent",
                    JsObject(// Objects are the mapping type in JSON.
                      Seq(
                        ("numVals", JsNumber(0)),
                        ("numFuncs", JsNumber(0)),
                        ("numObjects", JsNumber(0)),
                        ("height", JsNumber(0)),
                        ("maxExpressionsInFunc", JsNumber(0)),
                        ("maxFuncsInObject", JsNumber(0)),
                        ("maxParamsInFunc", JsNumber(0)),
                        ("height", JsNumber(0)),
                        ("maxObjectsInTree", JsNumber(0))
                      )
                    )
                    )
                )
              )
              )
          )
        )
      )

      Json.toJson(Map("key" -> Right(Seq[Int](0, 1, 2)))) must equal(
        JsObject(
          Seq(
            ("key",
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
          ("Scope(0,0,0,1,0,0,0,0)",
            JsObject(
              Seq(
                ("intContent", JsArray(Seq(JsNumber(0), JsNumber(1), JsNumber(2))))
              )
            )
            )
        )
      )

      Json.toJson(Map[IScope, Either[CountDownLatch, Seq[Int]]](Scope() -> left)) must equal(JsObject(Seq.empty))

      Json.toJson(Map[IScope, Either[CountDownLatch, Seq[Int]]](Scope(height = 1) -> right)) must equal(expected)

      Json.toJson(Map[IScope, Either[CountDownLatch, Seq[Int]]](
        Scope() -> left,
        Scope(height = 1) -> right
      )
      ) must equal(expected)
    }
  }

  private val scopeAsJsonString = """{"numVals":0,"numFuncs":0,"numObjects":0,"height":0,"maxExpressionsInFunc":0,"maxFuncsInObject":0,"maxParamsInFunc":0,"maxObjectsInTree":0}"""
  private val scopeAsModel: IScope = Scope()
}