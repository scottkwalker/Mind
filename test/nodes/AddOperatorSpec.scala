package nodes

import org.specs2.mutable._
import nodes.helpers._
import org.specs2.mock.Mockito
import com.google.inject.{Guice, Injector}
import ai.helpers.TestAiModule
import nodes.helpers.Scope
import ai.IRandomNumberGenerator

class AddOperatorSpec extends Specification with Mockito {
  "AddOperator" should {
    "toRawScala" in {
      val a = mock[ValueRef]
      a.toRawScala returns "STUB_A"
      val b = mock[ValueRef]
      b.toRawScala returns "STUB_B"

      AddOperator(a, b).toRawScala mustEqual "STUB_A + STUB_B"
    }

    "validate" in {
      "true given child nodes can terminate in under N steps" in {
        val s = Scope(maxDepth = 2)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        AddOperator(v, v).validate(s) mustEqual true
      }

      "false given it cannot terminate in 0 steps" in {
        val s = Scope(maxDepth = 0)
        val v = mock[ValueRef]
        v.validate(any[Scope]) throws new RuntimeException

        AddOperator(v, v).validate(s) mustEqual false
      }

      "false given child nodes cannot terminate in under N steps" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns false

        AddOperator(v, v).validate(s) mustEqual false
      }

      "true given none empty" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        AddOperator(v, v).validate(s) mustEqual true
      }

      "false given contains an empty node" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        AddOperator(v, Empty()).validate(s) mustEqual false
      }

      "false given contains a node that is not valid for this level" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        AddOperator(v, ObjectDef(Nil, "ObjectM0")).validate(s) mustEqual false
      }
    }

    "replaceEmpty" in {
      "calls replaceEmpty on non-empty child nodes" in {
        val s = mock[Scope]
        val v = mock[ValueRef]
        v.replaceEmpty(any[Scope], any[Injector]) returns v
        val i = mock[Injector]
        val instance = AddOperator(v, v)

        instance.replaceEmpty(s, i)

        there was two(v).replaceEmpty(any[Scope], any[Injector])
      }

      "returns same when no empty nodes" in {
        val s = mock[Scope]
        val v = mock[ValueRef]
        v.replaceEmpty(any[Scope], any[Injector]) returns v
        val i = mock[Injector]
        val instance = AddOperator(v, v)

        instance.replaceEmpty(s, i) mustEqual instance
      }

      "returns without empty nodes given there were empty nodes" in {
        class TestDevModule extends DevModule(randomNumberGenerator = mock[IRandomNumberGenerator]) {
          override def bindAddOperatorFactory = {
            val n: Node = mock[ValueRef]
            val f = mock[AddOperatorFactory]
            f.create(any[Scope]) returns n
            bind(classOf[AddOperatorFactory]).toInstance(f)
          }
        }

        val s = mock[Scope]
        s.numVals returns 1
        val v = mock[Empty]
        val injector: Injector = Guice.createInjector(new TestDevModule, new TestAiModule)
        val instance = AddOperator(v, v)

        val result = instance.replaceEmpty(s, injector)

        result must beLike {
          case AddOperator(l, r) => {
            l must beAnInstanceOf[ValueRef]
            r must beAnInstanceOf[ValueRef]
          }
        }
      }

      "getMaxDepth returns 1 + child getMaxDepth" in {
        val v = mock[ValueRef]
        v.getMaxDepth returns 1

        AddOperator(v, v).getMaxDepth mustEqual 2
      }
    }
  }
}