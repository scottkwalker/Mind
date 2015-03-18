package decision

import composition.DecisionBindings
import composition.StubCreateNodeBinding
import composition.StubCreateSeqNodesBinding
import composition.StubLookupChildrenWithFutures
import composition.StubSelectionStrategyBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.domain.Step
import models.domain.scala.ValDclInFunctionParam
import org.mockito.Mockito._
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

final class ValDclInFunctionParamFactorySpec extends UnitTestHelpers with TestComposition {

  "create step" must {
    "return an instance of the expected type" in {
      val (valDclInFunctionParamFactory, scope) = build
      when(scope.maxParamsInFunc).thenReturn(1)

      val instruction = valDclInFunctionParamFactory.createStep(scope = scope)

      whenReady(instruction) {
        _ mustBe a[ValDclInFunctionParam]
      }(config = patienceConfig)
    }

    "returns expected if scope has 0 existing vals" in {
      val (valDclInFunctionParamFactory, scope) = build
      when(scope.maxParamsInFunc).thenReturn(1)

      val instruction = valDclInFunctionParamFactory.createStep(scope = scope)

      whenReady(instruction) {
        case ValDclInFunctionParam(name, primitiveType) =>
          name must equal("v0")
          primitiveType mustBe a[Step]
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "returns expected if scope has 1 existing val" in {
      val (valDclInFunctionParamFactory, scope) = build
      when(scope.numVals).thenReturn(1)
      when(scope.maxParamsInFunc).thenReturn(2)

      val instruction = valDclInFunctionParamFactory.createStep(scope = scope)

      whenReady(instruction) {
        case ValDclInFunctionParam(name, primitiveType) =>
          name must equal("v1")
          primitiveType mustBe a[Step]
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }
  }

  "createParams" must {
    "throw an exception" in {
      val (valDclInFunctionParamFactory, scope) = build

      a[RuntimeException] must be thrownBy valDclInFunctionParamFactory.createParams(scope).futureValue
    }
  }

  "createNodes" must {
    "throw an exception" in {
      val (valDclInFunctionParamFactory, scope) = build

      a[RuntimeException] must be thrownBy valDclInFunctionParamFactory.createNodes(scope).futureValue
    }
  }

  "updateScope" must {
    "calls increments vals once" in {
      val (valDclInFunctionParamFactory, scope) = build

      valDclInFunctionParamFactory.updateScope(scope)

      verify(scope, times(1)).incrementVals
      verifyNoMoreInteractions(scope)
    }
  }

  private def build = {
    val scope = mock[IScope]
    val valDclInFunctionParamFactory = testInjector(
      new DecisionBindings,
      new StubLookupChildrenWithFutures,
      new StubCreateNodeBinding,
      new StubCreateSeqNodesBinding,
      new StubSelectionStrategyBinding
    ).getInstance(classOf[ValDclInFunctionParamFactory])
    (valDclInFunctionParamFactory, scope)
  }
}