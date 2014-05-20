package nodes

import org.specs2.mutable._
import nodes.helpers.{IScope, Scope}
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import models.domain.scala.{ValDclInFunctionParam, IntegerM}

class ValDclInFunctionParamFactorySpec extends Specification with Mockito {
  "ValDclInFunctionParamFactory" should {
    val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
    val factory = injector.getInstance(classOf[ValDclInFunctionParamFactory])

    "create" in {
      "returns instance of this type" in {
        val s = Scope(maxDepth = 10, maxParamsInFunc = 1)

        val instance = factory.create(scope = s)

        instance must beAnInstanceOf[ValDclInFunctionParam]
      }

      "returns expected given scope with 0 vals" in {
        val s = Scope(numVals = 0, maxParamsInFunc = 1, maxDepth = 10)

        val instance = factory.create(scope = s)

        instance must beLike {
          case ValDclInFunctionParam(name, primitiveType) =>
            name mustEqual "v0"
            primitiveType must beAnInstanceOf[IntegerM]
        }
      }

      "returns expected given scope with 1 val" in {
        val s = Scope(numVals = 1, maxParamsInFunc = 2, maxDepth = 10)

        val instance = factory.create(scope = s)

        instance must beLike {
          case ValDclInFunctionParam(name, primitiveType) =>
            name mustEqual "v1"
            primitiveType must beAnInstanceOf[IntegerM]
        }
      }
    }

    "updateScope increments vals" in {
      val s = mock[IScope]

      factory.updateScope(s)

      there was one(s).incrementVals
    }
  }
}