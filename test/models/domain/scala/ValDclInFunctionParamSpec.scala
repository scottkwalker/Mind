package models.domain.scala

import com.google.inject.{AbstractModule, Injector}

import composition.{StubReplaceEmpty, TestComposition}
import factory.{ValDclInFunctionParamFactory, ValDclInFunctionParamFactoryImpl}
import models.common.{IScope, Scope}
import models.domain.Node
import org.mockito.Matchers._
import org.mockito.Mockito._

final class ValDclInFunctionParamSpec extends TestComposition {

  "toRawScala" must {
    "return expected" in {
      val p = mock[Node]
      when(p.toRaw).thenReturn("Int")
      val name = "a"

      ValDclInFunctionParam(name, p).toRaw must equal("a: Int")
    }
  }

  "hasNoEmpty" must {
    "false given it cannot terminate in under N steps" in {
      val s = mock[IScope]
      when(s.hasHeightRemaining).thenReturn(false)
      val name = "a"
      val p = mock[Node]

      ValDclInFunctionParam(name, p).hasNoEmpty(s) must equal(false)
    }

    "false given an empty name" in {
      val s = mock[IScope]
      when(s.hasHeightRemaining).thenReturn(true)
      val name = ""
      val p = mock[Node]

      ValDclInFunctionParam(name, p).hasNoEmpty(s) must equal(false)
    }

    "false given an invalid child" in {
      val s = mock[IScope]
      when(s.hasHeightRemaining).thenReturn(true)
      val name = "a"
      val p = mock[Node]
      when(p.hasNoEmpty(any[Scope])).thenReturn(false)

      ValDclInFunctionParam(name, p).hasNoEmpty(s) must equal(false)
    }

    "true given it can terminate, has a non-empty name and valid child" in {
      val s = mock[IScope]
      when(s.hasHeightRemaining).thenReturn(true)
      val name = "a"
      val p = IntegerM()

      ValDclInFunctionParam(name, p).hasNoEmpty(s) must equal(true)
    }
  }

  "replaceEmpty" must {
    "calls replaceEmpty on non-empty child nodes" in {
      val s = mock[IScope]
      when(s.incrementVals).thenReturn(s)
      when(s.decrementHeight).thenReturn(s)
      val name = "a"
      implicit val i = mock[Injector]
      val p = mock[Node]
      when(p.replaceEmpty(any[Scope])(any[Injector])).thenReturn(p)
      val instance = ValDclInFunctionParam(name, p)

      instance.replaceEmpty(s)

      verify(p, times(1)).replaceEmpty(any[Scope])(any[Injector])
    }

    "returns same when no empty nodes" in {
      val s = mock[IScope]
      when(s.incrementVals).thenReturn(s)
      when(s.decrementHeight).thenReturn(s)
      val name = "a"
      implicit val i = mock[Injector]
      val p = mock[Node]
      when(p.replaceEmpty(any[Scope])(any[Injector])).thenReturn(p)
      val instance = ValDclInFunctionParam(name, p)

      val result = instance.replaceEmpty(s)

      result must equal(instance)
    }

    "returns without empty nodes given there were empty nodes" in {
      val s = mock[IScope]
      val name = "a"
      val primitiveTypeEmpty = Empty()
      val i = testInjector(new StubReplaceEmpty)
      val instance = ValDclInFunctionParam(name, primitiveTypeEmpty)

      val result = instance.replaceEmpty(s)(i)

      result match {
        case ValDclInFunctionParam(name2, primitiveType) =>
          name2 must equal("a")
          primitiveType mustBe an[IntegerM]
        case _ => fail("wrong type")
      }
    }
  }

  "height" must {
    "returns 1 + child height" in {
      val name = "a"
      val p = mock[Node]
      when(p.height).thenReturn(1)

      ValDclInFunctionParam(name, p).height must equal(2)
    }
  }
}