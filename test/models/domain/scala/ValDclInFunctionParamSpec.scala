package models.domain.scala

import com.google.inject.Injector
import composition.{StubReplaceEmpty, TestComposition}
import models.common.{IScope, Scope}
import models.domain.Instruction
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class ValDclInFunctionParamSpec extends TestComposition {

  "toRawScala" must {
    "return expected" in {
      val instruction = mock[Instruction]
      when(instruction.toRaw).thenReturn("Int")
      val name = "a"

      ValDclInFunctionParam(name, instruction).toRaw must equal("a: Int")
    }
  }

  "hasNoEmpty" must {
    "false given it cannot terminate in under N steps" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(false)
      val name = "a"
      val instruction = mock[Instruction]

      ValDclInFunctionParam(name, instruction).hasNoEmpty(scope) must equal(false)
    }

    "false given an empty name" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val name = ""
      val instruction = mock[Instruction]

      ValDclInFunctionParam(name, instruction).hasNoEmpty(scope) must equal(false)
    }

    "false given an invalid child" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val name = "a"
      val instruction = mock[Instruction]
      when(instruction.hasNoEmpty(any[Scope])).thenReturn(false)

      ValDclInFunctionParam(name, instruction).hasNoEmpty(scope) must equal(false)
    }

    "true given it can terminate, has a non-empty name and valid child" in {
      val scope = mock[IScope]
      when(scope.hasHeightRemaining).thenReturn(true)
      val name = "a"
      val instruction = IntegerM()

      ValDclInFunctionParam(name, instruction).hasNoEmpty(scope) must equal(true)
    }
  }

  "replaceEmpty" must {
    "calls replaceEmpty on non-empty child nodes" in {
      val scope = mock[IScope]
      when(scope.incrementVals).thenReturn(scope)
      when(scope.decrementHeight).thenReturn(scope)
      val name = "a"
      val injector = mock[Injector]
      val instruction = mock[Instruction]
      when(instruction.replaceEmpty(any[Scope])(any[Injector])) thenReturn Future.successful(instruction)
      val valDclInFunctionParam = ValDclInFunctionParam(name, instruction)

      val result = valDclInFunctionParam.replaceEmpty(scope)(injector)

      whenReady(result) { _ => verify(instruction, times(1)).replaceEmpty(any[Scope])(any[Injector])}(config = patienceConfig)
    }

    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      when(scope.incrementVals).thenReturn(scope)
      when(scope.decrementHeight).thenReturn(scope)
      val name = "a"
      val injector = mock[Injector]
      val instruction = mock[Instruction]
      when(instruction.replaceEmpty(any[Scope])(any[Injector])) thenReturn Future.successful(instruction)
      val valDclInFunctionParam = ValDclInFunctionParam(name, instruction)

      val result = valDclInFunctionParam.replaceEmpty(scope)(injector)

      whenReady(result) {
        _ must equal(valDclInFunctionParam)
      }(config = patienceConfig)
    }

    "returns without empty nodes given there were empty nodes" in {
      val scope = mock[IScope]
      val name = "a"
      val primitiveTypeEmpty = Empty()
      val injector = testInjector(new StubReplaceEmpty)
      val valDclInFunctionParam = ValDclInFunctionParam(name, primitiveTypeEmpty)

      val result = valDclInFunctionParam.replaceEmpty(scope)(injector)

      whenReady(result) {
        case ValDclInFunctionParam(name2, primitiveType) =>
          name2 must equal("a")
          primitiveType mustBe an[IntegerM]
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }
  }

  "height" must {
    "returns 1 + child height" in {
      val name = "a"
      val instruction = mock[Instruction]
      when(instruction.height).thenReturn(1)

      ValDclInFunctionParam(name, instruction).height must equal(2)
    }
  }
}