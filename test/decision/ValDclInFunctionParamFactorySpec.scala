package decision

import composition.TestComposition
import models.common.Scope
import models.domain.scala.ValDclInFunctionParam

final class ValDclInFunctionParamFactorySpec extends TestComposition {

  "create step" must {
    "returns instance of this type" in {
      val scope = Scope(height = 10, maxParamsInFunc = 1, maxHeight = 10)

      val instruction = valDclInFunctionParamFactory.createStep(scope = scope)

      whenReady(instruction) { result =>
        result mustBe a[ValDclInFunctionParam]
      }(config = patienceConfig)
    }

    //    "returns expected given scope with 0 vals" in {
    //      val scope = Scope(numVals = 0, maxParamsInFunc = 1, height = 10, maxHeight = 10)
    //
    //      val instruction = valDclInFunctionParamFactory.create(scope = scope)
    //
    //      whenReady(instruction) {
    //        case ValDclInFunctionParam(name, primitiveType) =>
    //          name must equal("v0")
    //          primitiveType mustBe a[IntegerM]
    //        case _ => fail("wrong type")
    //      }(config = patienceConfig)
    //    }
    //
    //    "returns expected given scope with 1 val" in {
    //      val scope = Scope(numVals = 1, maxParamsInFunc = 2, height = 10, maxHeight = 10)
    //
    //      val instruction = valDclInFunctionParamFactory.create(scope = scope)
    //
    //      whenReady(instruction) {
    //        case ValDclInFunctionParam(name, primitiveType) =>
    //          name must equal("v1")
    //          primitiveType mustBe a[IntegerM]
    //        case _ => fail("wrong type")
    //      }(config = patienceConfig)
    //    }
  }

  //  "updateScope" must {
  //    "calls increments vals once" in {
  //      val scope = mock[IScope]
  //
  //      valDclInFunctionParamFactory.updateScope(scope)
  //
  //      verify(scope, times(1)).incrementVals
  //    }
  //  }

  private def valDclInFunctionParamFactory = testInjector().getInstance(classOf[ValDclInFunctionParamFactoryImpl])
}