package factory

import composition.TestComposition
import models.common.{IScope, Scope}
import models.domain.scala.{IntegerM, ValDclInFunctionParam}
import org.mockito.Mockito._

final class ValDclInFunctionParamFactorySpec extends TestComposition {

  "create" must {
    "returns instance of this type" in {
      val s = Scope(height = 10, maxParamsInFunc = 1)

      val instance = factory.create(scope = s)

      instance mustBe a[ValDclInFunctionParam]
    }

    "returns expected given scope with 0 vals" in {
      val s = Scope(numVals = 0, maxParamsInFunc = 1, height = 10)

      val instance = factory.create(scope = s)

      instance match {
        case ValDclInFunctionParam(name, primitiveType) =>
          name must equal("v0")
          primitiveType mustBe a[IntegerM]
        case _ => fail("wrong type")
      }
    }

    "returns expected given scope with 1 val" in {
      val s = Scope(numVals = 1, maxParamsInFunc = 2, height = 10)

      val instance = factory.create(scope = s)

      instance match {
        case ValDclInFunctionParam(name, primitiveType) =>
          name must equal("v1")
          primitiveType mustBe a[IntegerM]
        case _ => fail("wrong type")
      }
    }
  }

  "updateScope" must {
    "calls increments vals once" in {
      val s = mock[IScope]

      factory.updateScope(s)

      verify(s, times(1)).incrementVals
    }
  }

  private val factory = testInjector().getInstance(classOf[ValDclInFunctionParamFactoryImpl])
}