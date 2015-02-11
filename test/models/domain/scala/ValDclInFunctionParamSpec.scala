package models.domain.scala

import composition.TestComposition
import models.common.IScope
import models.common.Scope
import models.domain.Step
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class ValDclInFunctionParamSpec extends TestComposition {

  "toCompilable" must {
    "return expected" in {
      val instruction = mock[Step]
      when(instruction.toCompilable).thenReturn("Int")
      val name = "a"

      val compilable = ValDclInFunctionParam(name, instruction).toCompilable

      compilable must equal("a: Int")
    }
  }

  "hasNoEmptySteps" must {
    "false given it cannot terminate in under N steps" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(false)
      val name = "a"
      val instruction = mock[Step]

      val hasNoEmptySteps = ValDclInFunctionParam(name, instruction).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "false given an empty name" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val name = ""
      val instruction = mock[Step]

      val hasNoEmptySteps = ValDclInFunctionParam(name, instruction).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "false given an invalid child" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val name = "a"
      val instruction = mock[Step]
      when(instruction.hasNoEmptySteps(any[Scope])).thenReturn(false)

      val hasNoEmptySteps = ValDclInFunctionParam(name, instruction).hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }

    "true given it can terminate, has a non-empty name and valid child" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val name = "a"
      val instruction = IntegerM()

      val hasNoEmptySteps = ValDclInFunctionParam(name, instruction).hasNoEmptySteps(scope)

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
      val instruction = mock[Step]
      when(instruction.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(instruction)
      val valDclInFunctionParam = ValDclInFunctionParam(name, instruction)

      val step = valDclInFunctionParam.fillEmptySteps(scope, factoryLookup)

      whenReady(step) { _ => verify(instruction, times(1)).fillEmptySteps(any[Scope], any[FactoryLookup])}(config = patienceConfig)
    }

    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      when(scope.incrementVals).thenReturn(scope)
      when(scope.decrementHeight).thenReturn(scope)
      val name = "a"
      val factoryLookup = mock[FactoryLookup]
      val instruction = mock[Step]
      when(instruction.fillEmptySteps(any[Scope], any[FactoryLookup])) thenReturn Future.successful(instruction)
      val valDclInFunctionParam = ValDclInFunctionParam(name, instruction)

      val step = valDclInFunctionParam.fillEmptySteps(scope, factoryLookup)

      whenReady(step) {
        _ must equal(valDclInFunctionParam)
      }(config = patienceConfig)
    }

    "returns without empty nodes given there were empty nodes" in {
      val scope = mock[IScope]
      val name = "a"
      val primitiveTypeEmpty = Empty()
      val injector = testInjector().getInstance(classOf[FactoryLookup])
      val valDclInFunctionParam = ValDclInFunctionParam(name, primitiveTypeEmpty)

      val step = valDclInFunctionParam.fillEmptySteps(scope, injector)

      whenReady(step) {
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

      val height = ValDclInFunctionParam(name, instruction).height

      height must equal(2)
    }
  }
}