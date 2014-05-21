package nodes

import com.google.inject.{Guice, Injector}
import ai.IRandomNumberGenerator

import nodes.helpers.{IScope, Scope}
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule
import models.domain.scala.{Empty, ValDclInFunctionParam, IntegerM}
import models.domain.common.Node
import org.mockito.Mockito._
import org.mockito.Matchers._
import utils.helpers.UnitSpec

class ValDclInFunctionParamSpec extends UnitSpec {
  "toRawScala" should {
    "return expected" in {
      val p = mock[IntegerM]
      when(p.toRaw).thenReturn("Int")
      val name = "a"

      ValDclInFunctionParam(name, p).toRaw should equal("a: Int")
    }
  }

  "validate" should {
    "false given it cannot terminate in under N steps" in {
      val s = mock[IScope]
      when(s.hasDepthRemaining).thenReturn(false)
      val name = "a"
      val p = mock[IntegerM]

      ValDclInFunctionParam(name, p).validate(s) should equal(false)
    }

    "false given an empty name" in {
      val s = mock[IScope]
      when(s.hasDepthRemaining).thenReturn(true)
      val name = ""
      val p = mock[IntegerM]

      ValDclInFunctionParam(name, p).validate(s) should equal(false)
    }

    "false given an invalid child" in {
      val s = mock[IScope]
      when(s.hasDepthRemaining).thenReturn(true)
      val name = "a"
      val p = mock[IntegerM]
      when(p.validate(any[Scope])).thenReturn(false)

      ValDclInFunctionParam(name, p).validate(s) should equal(false)
    }

    "true given it can terminate, has a non-empty name and valid child" in {
      val s = mock[IScope]
      when(s.hasDepthRemaining).thenReturn(true)
      val name = "a"
      val p = mock[IntegerM]
      when(p.validate(any[Scope])).thenReturn(true)

      ValDclInFunctionParam(name, p).validate(s) should equal(true)
    }
  }

  "replaceEmpty" should {
    "calls replaceEmpty on non-empty child nodes" in {
      val s = mock[IScope]
      when(s.incrementVals).thenReturn(s)
      when(s.incrementDepth).thenReturn(s)
      val name = "a"
      val p = mock[IntegerM]
      when(p.replaceEmpty(any[Scope], any[Injector])).thenReturn(p)
      val i = mock[Injector]
      val instance = ValDclInFunctionParam(name, p)

      instance.replaceEmpty(s, i)

      verify(p, times(1)).replaceEmpty(any[Scope], any[Injector])
    }

    "returns same when no empty nodes" in {
      val s = mock[IScope]
      when(s.incrementVals).thenReturn(s)
      when(s.incrementDepth).thenReturn(s)
      val name = "a"
      val p = mock[IntegerM]
      when(p.replaceEmpty(any[Scope], any[Injector])).thenReturn(p)
      val i = mock[Injector]
      val instance = ValDclInFunctionParam(name, p)

      val result = instance.replaceEmpty(s, i)

      result should equal(instance)
    }

    "returns without empty nodes given there were empty nodes" in {
      class TestDevModule extends DevModule(randomNumberGenerator = mock[IRandomNumberGenerator]) {
        override def bindValDclInFunctionParamFactory(): Unit = {
          val n: Node = mock[IntegerM]
          val f = mock[ValDclInFunctionParamFactory]
          when(f.create(any[Scope])).thenReturn(n)
          bind(classOf[ValDclInFunctionParamFactory]).toInstance(f)
        }
      }

      val s = mock[IScope]
      val name = "a"
      val p = mock[Empty]
      val injector: Injector = Guice.createInjector(new TestDevModule, new LegalGamerModule)
      val instance = ValDclInFunctionParam(name, p)

      val result = instance.replaceEmpty(s, injector)

      result match {
        case ValDclInFunctionParam(name2, primitiveType) =>
          name2 should equal("a")
          primitiveType shouldBe an[IntegerM]
        case _ => fail("wrong type")
      }
    }
  }

  "getMaxDepth" should {
    "returns 1 + child getMaxDepth" in {
      val name = "a"
      val p = mock[IntegerM]
      when(p.getMaxDepth).thenReturn(1)

      ValDclInFunctionParam(name, p).getMaxDepth should equal(2)
    }
  }
}