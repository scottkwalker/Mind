package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import java.lang.IllegalArgumentException
import com.google.inject.{Guice, Injector}
import com.tzavellas.sse.guice.ScalaModule

class FunctionMSpec extends Specification with Mockito {
  "Function" should {
    val name = "f0"
    val params = Seq(ValueInFunctionParam("a", IntegerM()), ValueInFunctionParam("b", IntegerM()))

    "validate" in {
      "false given an empty name" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true
        FunctionM(params = params,
          nodes = Seq(v, v),
          name = "").validate(s) mustEqual false
      }

      "true given it can terminate in under N steps" in {
        val s = Scope(maxDepth = 3)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        FunctionM(params = params,
          nodes = Seq(v, v),
          name = name).validate(s) mustEqual true
      }

      "false given it cannot terminate in 0 steps" in {
        val s = Scope(depth = 0)
        val v = mock[ValueRef]
        v.validate(any[Scope]) throws new RuntimeException

        FunctionM(params = params,
          nodes = Seq(v, v),
          name = name).validate(s) mustEqual false
      }

      "false given it cannot terminate in under N steps" in {
        val s = Scope(depth = 2)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns false

        FunctionM(params = params,
          nodes = Seq(v, v),
          name = name).validate(s) mustEqual false
      }

      "true given no empty nodes" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        FunctionM(params = params,
          nodes = Seq(v, v),
          name = name).validate(s) mustEqual true
      }

      "false given an empty node" in {
        val s = Scope(maxDepth = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        FunctionM(params = params,
          nodes = Seq(v, Empty()),
          name = name).validate(s) mustEqual false
      }
    }

    "toRawScala" in {
      "returns expected" in {
        val a = mock[ValueRef]
        a.toRawScala returns "STUB"

        FunctionM(params = params,
          nodes = Seq(a),
          name = name).toRawScala mustEqual "def f0(a: Int, b: Int) = { STUB }"
      }

      "throws if has no name" in {
        val a = mock[ValueRef]
        a.toRawScala returns "STUB"

        FunctionM(params = params,
          nodes = Seq(a),
          name = "").toRawScala must throwA[IllegalArgumentException]
      }
    }

    "replaceEmpty" in {
      "calls replaceEmpty on non-empty child nodes" in {
        val s = mock[Scope]
        val p = mock[ValueInFunctionParam]
        p.replaceEmpty(any[Scope], any[Injector]) returns p
        val v = mock[ValueRef]
        v.replaceEmpty(any[Scope], any[Injector]) returns v
        val i = mock[Injector]
        val instance = FunctionM(params = Seq(p),
          nodes = Seq(v),
          name = name)

        instance.replaceEmpty(s, i)

        there was one(p).replaceEmpty(any[Scope], any[Injector])
        there was one(v).replaceEmpty(any[Scope], any[Injector])
      }

      "returns same when no empty nodes" in {
        val s = mock[Scope]
        val p = mock[ValueInFunctionParam]
        p.replaceEmpty(any[Scope], any[Injector]) returns p
        val v = mock[ValueRef]
        v.replaceEmpty(any[Scope], any[Injector]) returns v
        val i = mock[Injector]
        val instance = FunctionM(params = Seq(p),
          nodes = Seq(v),
          name = name)

        instance.replaceEmpty(s, i) mustEqual instance
      }

      "returns without empty nodes given there were empty nodes" in {
        class TestDevModule extends ScalaModule {
          def configure() {
            val n: Node = mock[ValueRef]
            val f = mock[FunctionMFactory]
            f.create(any[Scope]) returns n
            bind(classOf[FunctionMFactory]).toInstance(f)
          }
        }

        val s = mock[Scope]
        val p = mock[Empty]
        val v = mock[Empty]
        val injector: Injector = Guice.createInjector(new TestDevModule)
        val instance = FunctionM(params = Seq(p),
          nodes = Seq(v),
          name = name)

        val result = instance.replaceEmpty(s, injector)

        result must beLike {
          case FunctionM(params, nodes, n) => {
            params must beLike {
              case Seq(p) => p must beAnInstanceOf[ValueRef]
            }
            nodes must beLike {
              case Seq(n) => n must beAnInstanceOf[ValueRef]
            }
            n mustEqual name
          }
        }
      }
    }
  }
}