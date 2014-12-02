package replaceEmpty

import composition.TestComposition
import models.common.{IScope, Scope}
import models.domain.scala.{IntegerM, ValDclInFunctionParam}
import org.mockito.Mockito._

final class ValDclInFunctionParamFactorySpec extends TestComposition {

  "create" must {
    "returns instance of this type" in {
      val scope = Scope(height = 10, maxParamsInFunc = 1)

      val instruction = valDclInFunctionParamFactory.create(scope = scope)

      whenReady(instruction) { result =>
        result mustBe a[ValDclInFunctionParam]
      }
    }

    "returns expected given scope with 0 vals" in {
      val scope = Scope(numVals = 0, maxParamsInFunc = 1, height = 10)

      val instruction = valDclInFunctionParamFactory.create(scope = scope)

      whenReady(instruction) {
        case ValDclInFunctionParam(name, primitiveType) =>
          name must equal("v0")
          primitiveType mustBe a[IntegerM]
        case _ => fail("wrong type")
      }
    }

    "returns expected given scope with 1 val" in {
      val scope = Scope(numVals = 1, maxParamsInFunc = 2, height = 10)

      val instruction = valDclInFunctionParamFactory.create(scope = scope)

      whenReady(instruction) {
        case ValDclInFunctionParam(name, primitiveType) =>
          name must equal("v1")
          primitiveType mustBe a[IntegerM]
        case _ => fail("wrong type")
      }
    }
  }

  "updateScope" must {
    "calls increments vals once" in {
      val scope = mock[IScope]

      valDclInFunctionParamFactory.updateScope(scope)

      verify(scope, times(1)).incrementVals
    }
  }

  private val valDclInFunctionParamFactory = testInjector().getInstance(classOf[ValDclInFunctionParamFactoryImpl])
}