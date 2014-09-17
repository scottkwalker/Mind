package models.domain.scala

import com.google.inject.{AbstractModule, Injector}

import composition.TestComposition
import factory.{AddOperatorFactory, AddOperatorFactoryImpl}
import models.common.{IScope, Scope}
import models.domain.Node
import org.mockito.Matchers._
import org.mockito.Mockito._

final class AddOperatorSpec extends TestComposition {

  "toRawScala" must {
    "return expected" in {
      val a = mock[Node]
      when(a.toRaw).thenReturn("STUB_A")
      val b = mock[Node]
      when(b.toRaw).thenReturn("STUB_B")

      AddOperator(a, b).toRaw must equal("STUB_A + STUB_B")
    }
  }

  "hasNoEmpty" must {
    "true given child nodes can terminate in under N steps" in {
      val s = Scope(height = 2)
      val v = ValueRef("v")

      AddOperator(v, v).hasNoEmpty(s) must equal(true)
    }

    "false given it cannot terminate in 0 steps" in {
      val s = Scope(height = 0)
      val v = mock[Node]
      when(v.hasNoEmpty(any[Scope])).thenThrow(new RuntimeException)

      AddOperator(v, v).hasNoEmpty(s) must equal(false)
    }

    "false given child nodes cannot terminate in under N steps" in {
      val s = Scope(height = 10)
      val v = mock[Node]
      when(v.hasNoEmpty(any[Scope])).thenReturn(false)

      AddOperator(v, v).hasNoEmpty(s) must equal(false)
    }

    "true given none empty" in {
      val s = Scope(height = 10)
      val v = ValueRef("v")

      AddOperator(v, v).hasNoEmpty(s) must equal(true)
    }

    "false when left node is empty" in {
      val s = Scope(height = 10)
      val v = ValueRef("stub")

      AddOperator(Empty(), v).hasNoEmpty(s) must equal(false)
    }

    "false when right node is empty" in {
      val s = Scope(height = 10)
      val v = ValueRef("stub")

      AddOperator(v, Empty()).hasNoEmpty(s) must equal(false)
    }

    "false given contains a node that is not valid for this level" in {
      val s = Scope(height = 10)
      val v = mock[Node]
      when(v.hasNoEmpty(any[Scope])).thenReturn(true)

      AddOperator(v, ObjectDef(Seq.empty, "ObjectM0")).hasNoEmpty(s) must equal(false)
    }
  }

  "replaceEmpty" must {
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

      instance.replaceEmpty(s)(i) must equal(instance)
    }

    "returns without empty nodes given there were empty nodes" in {
      final class StubFactoryCreate extends AbstractModule {

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
          left mustBe a[ValueRef]
          right mustBe a[ValueRef]
      }
    }
  }

  "height" must {
    "return 1 + child height" in {
      val v = mock[Node]
      when(v.height).thenReturn(1)

      AddOperator(v, v).height must equal(2)
    }
  }
}