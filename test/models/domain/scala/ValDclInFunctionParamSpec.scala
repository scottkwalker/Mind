package models.domain.scala

import composition.TestHelpers
import composition.TestComposition
import models.common.IScope
import models.common.Scope
import models.domain.Step
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class ValDclInFunctionParamSpec extends TestHelpers with TestComposition {

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
    "false given it cannot terminate in under N steps" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(false)
      val name = "a"
      val step = mock[Step]
      val valDclInFunctionParam = ValDclInFunctionParam(name, step)

      val hasNoEmptySteps = valDclInFunctionParam.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "false given an empty name" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val name = ""
      val step = mock[Step]
      val valDclInFunctionParam = ValDclInFunctionParam(name, step)

      val hasNoEmptySteps = valDclInFunctionParam.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "false given an invalid child" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val name = "a"
      val step = mock[Step]
      when(step.hasNoEmptySteps(any[Scope])).thenReturn(false)
      val valDclInFunctionParam = ValDclInFunctionParam(name, step)

      val hasNoEmptySteps = valDclInFunctionParam.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "true given it can terminate, has a non-empty name and valid child" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val name = "a"
      val integerM = IntegerM()
      val valDclInFunctionParam = ValDclInFunctionParam(name, integerM)

      val hasNoEmptySteps = valDclInFunctionParam.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }
  }

  "fillEmptySteps" must {
    "calls fillEmptySteps on non-empty child nodes" in {
      val scope = mock[IScope]
      when(scope.incrementVals).thenReturn(scope)
      when(scope.decrementHeight).thenReturn(scope)
      val name = "a"
      val factoryLookup = mock[FactoryLookup]
      val step = mock[Step]
      when(step.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(step)
      val valDclInFunctionParam = ValDclInFunctionParam(name, step)

      val fillEmptySteps = valDclInFunctionParam.fillEmptySteps(scope, factoryLookup)

      whenReady(fillEmptySteps) { _ =>
        verify(step, times(1)).fillEmptySteps(any[Scope], any[FactoryLookup])
      }(config = patienceConfig)
    }

    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      when(scope.incrementVals).thenReturn(scope)
      when(scope.decrementHeight).thenReturn(scope)
      val name = "a"
      val factoryLookup = mock[FactoryLookup]
      val step = mock[Step]
      when(step.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(step)
      val valDclInFunctionParam = ValDclInFunctionParam(name, step)

      val fillEmptySteps = valDclInFunctionParam.fillEmptySteps(scope, factoryLookup)

      whenReady(fillEmptySteps) {
        _ must equal(valDclInFunctionParam)
      }(config = patienceConfig)
    }

    "returns without empty nodes given there were empty nodes" in {
      val scope = mock[IScope]
      val name = "a"
      val primitiveTypeEmpty = Empty()
      val factoryLookup = testInjector().getInstance(classOf[FactoryLookup])
      val valDclInFunctionParam = ValDclInFunctionParam(name, primitiveTypeEmpty)

      val fillEmptySteps = valDclInFunctionParam.fillEmptySteps(scope, factoryLookup)

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