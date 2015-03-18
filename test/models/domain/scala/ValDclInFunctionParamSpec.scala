package models.domain.scala

import composition.StubFactoryLookupAnyBinding
import composition.StubSelectionStrategyBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.domain.Step
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future
import utils.ScopeHelper._

final class ValDclInFunctionParamSpec extends UnitTestHelpers with TestComposition {

  "toCompilable" must {
    "return expected" in {
      val step = mock[Step]
      when(step.toCompilable).thenReturn("Int")
      val name = "a"
      val valDclInFunctionParam = ValDclInFunctionParam(name, step)

      val compilable = valDclInFunctionParam.toCompilable

      compilable must equal("a: Int")
    }
  }

  "hasNoEmptySteps" must {
    "return false if it cannot terminate in under N steps" in {
      val name = "a"
      val step = mock[Step]
      val valDclInFunctionParam = ValDclInFunctionParam(name, step)

      val hasNoEmptySteps = valDclInFunctionParam.hasNoEmptySteps(scopeWithoutHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "return false if the name is empty" in {
      val name = ""
      val step = mock[Step]
      val valDclInFunctionParam = ValDclInFunctionParam(name, step)

      val hasNoEmptySteps = valDclInFunctionParam.hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "return false if there is an invalid type of child" in {
      val name = "a"
      val step = mock[Step]
      when(step.hasNoEmptySteps(any[IScope])).thenReturn(false)
      val valDclInFunctionParam = ValDclInFunctionParam(name, step)

      val hasNoEmptySteps = valDclInFunctionParam.hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(false)
    }

    "return true if it can terminate, has a non-empty name and valid type of child" in {
      val name = "a"
      val integerM = IntegerM()
      val valDclInFunctionParam = ValDclInFunctionParam(name, integerM)

      val hasNoEmptySteps = valDclInFunctionParam.hasNoEmptySteps(scopeWithHeightRemaining)

      hasNoEmptySteps must equal(true)
    }
  }

  "fillEmptySteps" must {
    "call fillEmptySteps once for each non-empty child node" in {
      val name = "a"
      val factoryLookup = mock[FactoryLookup]
      val step = mock[Step]
      when(step.fillEmptySteps(any[IScope], any[FactoryLookup])) thenReturn Future.successful(step)
      val valDclInFunctionParam = ValDclInFunctionParam(name, step)

      val fillEmptySteps = valDclInFunctionParam.fillEmptySteps(scope(), factoryLookup)

      whenReady(fillEmptySteps) { _ =>
        verify(step, times(1)).fillEmptySteps(any[IScope], any[FactoryLookup])
        verifyNoMoreInteractions(step)
      }(config = patienceConfig)
    }

    "return the same if there are no empty nodes" in {
      val name = "a"
      val factoryLookup = mock[FactoryLookup]
      val step = mock[Step]
      when(step.fillEmptySteps(any[IScope], any[FactoryLookup])) thenReturn Future.successful(step)
      val valDclInFunctionParam = ValDclInFunctionParam(name, step)

      val fillEmptySteps = valDclInFunctionParam.fillEmptySteps(scope(), factoryLookup)

      whenReady(fillEmptySteps) {
        _ must equal(valDclInFunctionParam)
      }(config = patienceConfig)
    }

    "return without empty nodes if there were empty nodes" in {
      val name = "a"
      val primitiveTypeEmpty = Empty()
      val factoryLookup = testInjector(
        new StubFactoryLookupAnyBinding,
        new StubSelectionStrategyBinding
      ).getInstance(classOf[FactoryLookup])
      val valDclInFunctionParam = ValDclInFunctionParam(name, primitiveTypeEmpty)

      val fillEmptySteps = valDclInFunctionParam.fillEmptySteps(scope(), factoryLookup)

      whenReady(fillEmptySteps) {
        case ValDclInFunctionParam(name2, primitiveType) =>
          name2 must equal("a")
          primitiveType mustBe a[Step]
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }
  }

  "height" must {
    "returns 1 + child height" in {
      val name = "a"
      val instruction = mock[Step]
      when(instruction.height).thenReturn(1)
      val valDclInFunctionParam = ValDclInFunctionParam(name, instruction)

      val height = valDclInFunctionParam.height

      height must equal(2)
    }
  }
}