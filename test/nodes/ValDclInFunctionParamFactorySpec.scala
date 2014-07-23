package nodes

import nodes.helpers.{IScope, Scope}
import com.google.inject.Injector
import com.google.inject.Guice
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import models.domain.scala.{ValDclInFunctionParam, IntegerM}
import org.mockito.Mockito._
import utils.helpers.UnitSpec

final class ValDclInFunctionParamFactorySpec extends UnitSpec {
  "create" should {
    "returns instance of this type" in {
      val s = Scope(height = 10, maxParamsInFunc = 1)

      val instance = factory.create(scope = s)

      instance shouldBe a[ValDclInFunctionParam]
    }

    "returns expected given scope with 0 vals" in {
      val s = Scope(numVals = 0, maxParamsInFunc = 1, height = 10)

      val instance = factory.create(scope = s)

      instance match {
        case ValDclInFunctionParam(name, primitiveType) =>
          name should equal("v0")
          primitiveType shouldBe a[IntegerM]
        case _ => fail("wrong type")
      }
    }

    "returns expected given scope with 1 val" in {
      val s = Scope(numVals = 1, maxParamsInFunc = 2, height = 10)

      val instance = factory.create(scope = s)

      instance match {
        case ValDclInFunctionParam(name, primitiveType) =>
          name should equal("v1")
          primitiveType shouldBe a[IntegerM]
        case _ => fail("wrong type")
      }
    }
  }

  "updateScope" should {
    "calls increments vals once" in {
      val s = mock[IScope]

      factory.updateScope(s)

      verify(s, times(1)).incrementVals
    }
  }

  private val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
  private val factory = injector.getInstance(classOf[ValDclInFunctionParamFactoryImpl])
}