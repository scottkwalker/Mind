package nodes.helpers

import com.google.inject.Injector
import com.google.inject.Guice
import modules.ai.aco.AcoModule
import modules.DevModule
import utils.helpers.UnitSpec
import play.api.libs.json._
import play.api.libs.json.Json.obj
import scala.collection.immutable.BitSet

final class ScopeSpec extends UnitSpec {
  "constructor" should {
    "set default values to zero" in {
      Scope() match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(0)
          numFuncs should equal(0)
          numObjects should equal(0)
          depth should equal(0)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "incrementVals" should {
    "return expected" in {
      Scope().incrementVals match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(1)
          numFuncs should equal(0)
          numObjects should equal(0)
          depth should equal(0)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "incrementFuncs" should {
    "return expected" in {
      Scope().incrementFuncs match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(0)
          numFuncs should equal(1)
          numObjects should equal(0)
          depth should equal(0)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "incrementObjects" should {
    "return expected" in {
      Scope().incrementObjects match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(0)
          numFuncs should equal(0)
          numObjects should equal(1)
          depth should equal(0)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "decrementStepsRemaining" should {
    "return expected" in {
      Scope(depth = 0).incrementDepth match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(0)
          numFuncs should equal(0)
          numObjects should equal(0)
          depth should equal(1)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "fluent interface" should {
    "return expected" in {
      Scope(depth = 0).
        incrementVals.
        incrementFuncs.
        incrementObjects.
        incrementDepth match {
        case Scope(numVals, numFuncs, numObjects, depth, maxExpressionsInFunc, maxFuncs, maxParamsInFunc, maxDepth, maxObjectsInTree) =>
          numVals should equal(1)
          numFuncs should equal(1)
          numObjects should equal(1)
          depth should equal(1)
          maxExpressionsInFunc should equal(0)
          maxFuncs should equal(0)
          maxParamsInFunc should equal(0)
          maxDepth should equal(0)
          maxObjectsInTree should equal(0)
        case _ => fail("should have matched")
      }
    }
  }

  "IoC create" should {
    "return a new instance with injected values" in {
      val injector: Injector = Guice.createInjector(new DevModule, new AcoModule)
      val sut = injector.getInstance(classOf[IScope])

      sut.maxFuncsInObject should equal(10)
      sut.maxExpressionsInFunc should equal(2)
    }
  }

  "serialize" should {
    "return expected json" in {
      jsonSerialiser.serialize(asModel) should equal(JsObject(Seq(
        ("numVals", JsNumber(0)),
        ("numFuncs", JsNumber(0)),
        ("numObjects", JsNumber(0)),
        ("depth", JsNumber(0)),
        ("maxExpressionsInFunc", JsNumber(0)),
        ("maxFuncsInObject", JsNumber(0)),
        ("maxParamsInFunc", JsNumber(0)),
        ("maxDepth", JsNumber(0)),
        ("maxObjectsInTree", JsNumber(0))
      )))
    }
  }

  "deserialize" should {
    "return expected mode" in {
      jsonSerialiser.deserialize[Scope](asJson) should equal(asModel)
    }
  }

  "experiment with json serialization" should {
    "Seq[Int]" in {
      jsonSerialiser.serialize(Seq[Int](0, 1, 2)) should equal(JsArray(Seq(JsNumber(0), JsNumber(1), JsNumber(2))))
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

      jsonSerialiser.serialize(Left("test")) should equal(JsObject(Seq(("stringContent", JsString("test")))))
      jsonSerialiser.serialize(Right(123)) should equal(JsObject(Seq(("intContent", JsNumber(123)))))
    }

    "Either[IScope, Int]" in {
      implicit val jsonWrites = new Writes[Either[IScope, Int]] {
        def writes(o: Either[IScope, Int]): JsValue = obj(
          o.fold(
            scopeContent => "scopeContent" -> jsonSerialiser.serialize(scopeContent),
            intContent => "intContent" -> JsNumber(intContent)
          )
        )
      }

      jsonSerialiser.serialize(Left(asModel)) should equal(
        JsObject(
          Seq(
            ("scopeContent",
              JsObject(
                Seq(
                  ("numVals", JsNumber(0)),
                  ("numFuncs", JsNumber(0)),
                  ("numObjects", JsNumber(0)),
                  ("depth", JsNumber(0)),
                  ("maxExpressionsInFunc", JsNumber(0)),
                  ("maxFuncsInObject", JsNumber(0)),
                  ("maxParamsInFunc", JsNumber(0)),
                  ("maxDepth", JsNumber(0)),
                  ("maxObjectsInTree", JsNumber(0))
                )
              )
              )
          )
        )
      )
      jsonSerialiser.serialize(Right(123)) should equal(JsObject(Seq(("intContent", JsNumber(123)))))
    }

    "Either[IScope, Seq[Int]]" in {
      implicit val jsonWrites = new Writes[Either[IScope, Seq[Int]]] {
        def writes(o: Either[IScope, Seq[Int]]): JsValue = obj(
          o.fold(
            scopeContent => "scopeContent" -> jsonSerialiser.serialize(scopeContent),
            intContent => "intContent" -> jsonSerialiser.serialize(intContent)
          )
        )
      }

      jsonSerialiser.serialize(Left(asModel)) should equal(
        JsObject(
          Seq(
            ("scopeContent",
              JsObject(
                Seq(
                  ("numVals", JsNumber(0)),
                  ("numFuncs", JsNumber(0)),
                  ("numObjects", JsNumber(0)),
                  ("depth", JsNumber(0)),
                  ("maxExpressionsInFunc", JsNumber(0)),
                  ("maxFuncsInObject", JsNumber(0)),
                  ("maxParamsInFunc", JsNumber(0)),
                  ("maxDepth", JsNumber(0)),
                  ("maxObjectsInTree", JsNumber(0))
                )
              )
              )
          )
        )
      )
      jsonSerialiser.serialize(Right(Seq[Int](0, 1, 2))) should equal(
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

      jsonSerialiser.serialize(Map[String, Int]("a" -> 1)) should equal(JsObject(Seq(
        ("a", JsNumber(1))
      )))
    }

    "Map[Int, Int]" in {
      implicit val jsonWrites = new Writes[Map[Int, Int]] {
        def writes(o: Map[Int, Int]): JsValue = {
          val keyAsString = o.map { kv => kv._1.toString -> kv._2 } // Convert to Map[String,Int] which it can convert
          Json.toJson(keyAsString)
        }
      }

      jsonSerialiser.serialize(Map[Int, Int](0 -> 1)) should equal(
        JsObject(
          Seq(
            ("0", JsNumber(1))
          )
        )
      )
    }

    "Map[Int, BitSet]" in {
      implicit val jsonWrites = new Writes[Map[Int, BitSet]] {
        def writes(o: Map[Int, BitSet]): JsValue = {
          val keyAsString = o.map { kv => kv._1.toString -> kv._2.toBitMask.mkString(".") } // Convert to Map[String,Int] which it can convert
          Json.toJson(keyAsString)
        }
      }

      jsonSerialiser.serialize(Map[Int, BitSet](1 -> (BitSet.empty + 1 + 2) )) should equal(
        JsObject(
          Seq(
            ("1", JsString("6"))
          )
        )
      )
    }
    //scala.collection.immutable.Map[TInput,Either[java.util.concurrent.CountDownLatch,TOutput]].
  }


  val jsonSerialiser = new JsonSerialiser
  val asJson = """{"numVals":0,"numFuncs":0,"numObjects":0,"depth":0,"maxExpressionsInFunc":0,"maxFuncsInObject":0,"maxParamsInFunc":0,"maxDepth":0,"maxObjectsInTree":0}"""
  val asModel: IScope = Scope()
}