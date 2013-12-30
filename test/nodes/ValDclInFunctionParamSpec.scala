package nodes

import org.specs2.mutable._
import nodes.helpers.{ICreateNode, CreateNode, MemoizeDi, Scope}
import org.specs2.mock.Mockito
import com.google.inject.{Guice, Injector}
import ai.helpers.TestAiModule
import com.tzavellas.sse.guice.ScalaModule
import ai.{RandomNumberGenerator, IRandomNumberGenerator}

class ValDclInFunctionParamSpec extends Specification with Mockito {
  "ValDclInFunctionParam" should {
    "toRawScala" in {
      val p = mock[IntegerM]
      p.toRawScala returns "Int"
      val name = "a"

      ValDclInFunctionParam(name, p).toRawScala mustEqual "a: Int"
    }

    "validate" in {
      "false given it cannot terminate in under N steps" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns false
        val name = "a"
        val p = mock[IntegerM]

        ValDclInFunctionParam(name, p).validate(s) mustEqual false
      }

      "false given an empty name" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns true
        val name = ""
        val p = mock[IntegerM]

        ValDclInFunctionParam(name, p).validate(s) mustEqual false
      }

      "false given an invalid child" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns true
        val name = "a"
        val p = mock[IntegerM]
        p.validate(any[Scope]) returns false

        ValDclInFunctionParam(name, p).validate(s) mustEqual false
      }

      "true given it can terminate, has a non-empty name and valid child" in {
        val s = mock[Scope]
        s.hasDepthRemaining returns true
        val name = "a"
        val p = mock[IntegerM]
        p.validate(any[Scope]) returns true

        ValDclInFunctionParam(name, p).validate(s) mustEqual true
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
        val instance = ValDclInFunctionParam(name, p)

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
        val instance = ValDclInFunctionParam(name, p)

        val result = instance.replaceEmpty(s, i)

        result mustEqual instance
      }

      "returns without empty nodes given there were empty nodes" in {
        class TestDevModule extends ScalaModule {
          def configure() {
            val n: Node = mock[IntegerM]
            val f = mock[ValDclInFunctionParamFactory]
            f.create(any[Scope]) returns n
            bind(classOf[ValDclInFunctionParamFactory]).toInstance(f)
            bind(classOf[MemoizeDi]).toInstance(MemoizeDi())
            bind(classOf[IRandomNumberGenerator]).toInstance(RandomNumberGenerator())
            bind(classOf[ICreateNode]).toInstance(CreateNode())
          }
        }

        val s = mock[Scope]
        val name = "a"
        val p = mock[Empty]
        val injector: Injector = Guice.createInjector(new TestDevModule, new TestAiModule)
        val instance = ValDclInFunctionParam(name, p)

        val result = instance.replaceEmpty(s, injector)

        result must beLike {
          case ValDclInFunctionParam(name2, primitiveType) => {
            name2 mustEqual "a"
            primitiveType must beAnInstanceOf[IntegerM]
          }
        }
      }
    }

    "getMaxDepth returns 1 + child getMaxDepth" in {
      val name = "a"
      val p = mock[IntegerM]
      p.getMaxDepth returns 1

      ValDclInFunctionParam(name, p).getMaxDepth mustEqual 2
    }
  }
}