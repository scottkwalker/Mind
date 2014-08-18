package models.domain.scala

import com.google.inject.Injector
import com.tzavellas.sse.guice.ScalaModule
import models.domain.common.Node
import nodes.helpers.{IScope, Scope}
import nodes.{AddOperatorFactory, AddOperatorFactoryImpl}
import org.mockito.Matchers._
import org.mockito.Mockito._
import utils.helpers.UnitSpec

final class AddOperatorSpec extends UnitSpec {

  "toRawScala" should {
    "return expected" in {
      val a = mock[Node]
      when(a.toRaw).thenReturn("STUB_A")
      val b = mock[Node]
      when(b.toRaw).thenReturn("STUB_B")

      AddOperator(a, b).toRaw should equal("STUB_A + STUB_B")
    }
  }

  "validate" should {
    "true given child nodes can terminate in under N steps" in {
      val s = Scope(height = 2)
      val v = ValueRef("v")

      AddOperator(v, v).validate(s) should equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val s = Scope(height = 0)
      val v = mock[Node]
      when(v.validate(any[Scope])).thenThrow(new RuntimeException)

      AddOperator(v, v).validate(s) should equal(false)
    }

    "false given child nodes cannot terminate in under N steps" in {
      val s = Scope(height = 10)
      val v = mock[Node]
      when(v.validate(any[Scope])).thenReturn(false)

      AddOperator(v, v).validate(s) should equal(false)
    }

    "true given none empty" in {
      val s = Scope(height = 10)
      val v = ValueRef("v")

      AddOperator(v, v).validate(s) should equal(true)
    }

    "false when left node is empty" in {
      val s = Scope(height = 10)
      val v = ValueRef("stub")

      AddOperator(Empty(), v).validate(s) should equal(false)
    }

    "false when right node is empty" in {
      val s = Scope(height = 10)
      val v = ValueRef("stub")

      AddOperator(v, Empty()).validate(s) should equal(false)
    }

    "false given contains a node that is not valid for this level" in {
      val s = Scope(height = 10)
      val v = mock[Node]
      when(v.validate(any[Scope])).thenReturn(true)

      AddOperator(v, ObjectDef(Seq.empty, "ObjectM0")).validate(s) should equal(false)
    }
  }

  "replaceEmpty" should {
    "calls replaceEmpty on non-empty child nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val v = mock[Node]
      when(v.replaceEmpty(any[Scope])(any[Injector])).thenReturn(v)
      val instance = AddOperator(v, v)

      instance.replaceEmpty(s)(i)

      verify(v, times(2)).replaceEmpty(any[Scope])(any[Injector])
    }

    "returns same when no empty nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val v = mock[Node]
      when(v.replaceEmpty(any[Scope])(any[Injector])).thenReturn(v)
      val instance = AddOperator(v, v)

      instance.replaceEmpty(s)(i) should equal(instance)
    }

    "returns without empty nodes given there were empty nodes" in {
      class StubFactoryCreate extends ScalaModule {

        def configure(): Unit = {
          val n: Node = mock[Node]
          val f = mock[AddOperatorFactoryImpl]
          when(f.create(any[Scope])).thenReturn(n)
          bind(classOf[AddOperatorFactory]).toInstance(f)
        }
      }

      val s = mock[IScope]
      when(s.numVals).thenReturn(1)
      val empty: Node = Empty()
      implicit val injector = testInjector(new StubFactoryCreate)
      val instance = AddOperator(empty, empty)

      val result = instance.replaceEmpty(s)

      result match {
        case AddOperator(left, right) =>
          left shouldBe a[ValueRef]
          right shouldBe a[ValueRef]
      }
    }
  }

  "getMaxDepth" should {
    "return 1 + child getMaxDepth" in {
      val v = mock[Node]
      when(v.getMaxDepth).thenReturn(1)

      AddOperator(v, v).getMaxDepth should equal(2)
    }
  }
}