package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.{Guice, Injector}
import ai.helpers.TestAiModule
import com.tzavellas.sse.guice.ScalaModule

class ValueInFunctionParamSpec extends Specification with Mockito {
  "ValueInFunctionParam" should {
    "toRawScala" in {
      val p = mock[IntegerM]
      p.toRawScala returns "Int"
      val name = "a"

      ValueInFunctionParam(name, p).toRawScala mustEqual "a: Int"
    }

    "validate" in {
      "false given it cannot terminate in under N steps" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns false
        val name = "a"
        val p = mock[IntegerM]

        ValueInFunctionParam(name, p).validate(s) mustEqual false
      }

      "false given an empty name" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns true
        val name = ""
        val p = mock[IntegerM]

        ValueInFunctionParam(name, p).validate(s) mustEqual false
      }

      "false given an invalid child" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns true
        val name = "a"
        val p = mock[IntegerM]
        p.validate(any[Scope]) returns false

        ValueInFunctionParam(name, p).validate(s) mustEqual false
      }

      "true given it can terminate, has a non-empty name and valid child" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns true
        val name = "a"
        val p = mock[IntegerM]
        p.validate(any[Scope]) returns true

        ValueInFunctionParam(name, p).validate(s) mustEqual true
      }
    }

    "replaceEmpty" in {
      "calls replaceEmpty on non-empty child nodes" in {
        val s = mock[Scope]
        s.incrementVals returns s
        s.incrementDepth returns s
        val name = "a"
        val p = mock[IntegerM]
        p.replaceEmpty(any[Scope], any[Injector]) returns p
        val i = mock[Injector]
        val instance = ValueInFunctionParam(name, p)

        instance.replaceEmpty(s, i)

        there was one(p).replaceEmpty(any[Scope], any[Injector])
      }

      "returns same when no empty nodes" in {
        val s = mock[Scope]
        s.incrementVals returns s
        s.incrementDepth returns s
        val name = "a"
        val p = mock[IntegerM]
        p.replaceEmpty(any[Scope], any[Injector]) returns p
        val i = mock[Injector]
        val instance = ValueInFunctionParam(name, p)

        val result = instance.replaceEmpty(s, i)

        result mustEqual instance
      }

      "returns without empty nodes given there were empty nodes" in {
        class TestDevModule extends ScalaModule {
          def configure() {
            val n: Node = mock[IntegerM]
            val f = mock[ValueInFunctionParamFactory]
            f.create(any[Scope]) returns n
            bind(classOf[ValueInFunctionParamFactory]).toInstance(f)
          }
        }

        val s = mock[Scope]
        val name = "a"
        val p = mock[Empty]
        val injector: Injector = Guice.createInjector(new TestDevModule, new TestAiModule)
        val instance = ValueInFunctionParam(name, p)

        val result = instance.replaceEmpty(s, injector)

        result must beLike {
          case ValueInFunctionParam(name, primitiveType) => {
            name mustEqual "a"
            primitiveType must beAnInstanceOf[IntegerM]
          }
        }
      }
    }

    "getMaxDepth returns 1 + child getMaxDepth" in {
      val name = "a"
      val p = mock[IntegerM]
      p.getMaxDepth returns 1

      ValueInFunctionParam(name, p).getMaxDepth mustEqual 2
    }
  }
}