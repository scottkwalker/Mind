package nodes

import org.specs2.mutable._
import nodes.helpers.{DevModule, Scope}
import org.specs2.mock.Mockito
import com.google.inject.{Guice, Injector}
import ai.helpers.TestAiModule

class ObjectDefSpec extends Specification with Mockito {
  "ObjectDef" should {
    val name = "o0"
    "validate" in {
      "true given it can terminates in under N steps" in {
        val s = Scope(maxDepth = 4)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns true
        val objectM = ObjectDef(Seq(f), name)

        objectM.validate(s) mustEqual true
      }

      "false given it cannot terminate in 0 steps" in {
        val s = Scope(depth = 0)
        val f = mock[FunctionM]
        f.validate(any[Scope]) throws new RuntimeException
        val objectM = ObjectDef(Seq(f), name)

        objectM.validate(s) mustEqual false
      }

      "false given it cannot terminate in under N steps" in {
        val s = Scope(depth = 3)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns false
        val objectM = ObjectDef(Seq(f), name)

        objectM.validate(s) mustEqual false
      }

      "true given no empty nodes" in {
        val s = Scope(maxDepth = 10)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns true
        val objectM = ObjectDef(Seq(f), name)

        objectM.validate(s) mustEqual true
      }

      "false given single empty method node" in {
        val s = Scope(maxDepth = 10)
        val objectM = ObjectDef(Seq(Empty()), name)
        objectM.validate(s) mustEqual false
      }

      "false given empty method node in a sequence" in {
        val s = Scope(maxDepth = 10)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns true
        val objectM = ObjectDef(Seq(f, Empty()), name)

        objectM.validate(s) mustEqual false
      }
    }

    "toRawScala" in {
      val f = mock[FunctionM]
      f.toRawScala returns "STUB"
      val objectM = ObjectDef(Seq(f), name)

      objectM.toRawScala mustEqual "object o0 { STUB }"
    }

    "replaceEmpty" in {
      "calls replaceEmpty on non-empty child nodes" in {
        val s = mock[Scope]
        val f = mock[FunctionM]
        f.replaceEmpty(any[Scope], any[Injector]) returns f
        val i = mock[Injector]
        val instance = ObjectDef(Seq(f), name = name)

        instance.replaceEmpty(s, i)

        there was one(f).replaceEmpty(any[Scope], any[Injector])
      }

      "returns same when no empty nodes" in {
        val s = mock[Scope]
        val f = mock[FunctionM]
        f.replaceEmpty(any[Scope], any[Injector]) returns f
        val i = mock[Injector]
        val instance = ObjectDef(Seq(f), name)

        instance.replaceEmpty(s, i) mustEqual instance
      }

      "returns without empty nodes given there were empty nodes" in {
        val s = Scope(maxExpressionsInFunc = 1,
          maxFuncsInObject = 1,
          maxParamsInFunc = 1,
          maxDepth = 5,
          maxObjectsInTree = 1)
        val n = mock[Empty]
        val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)
        val instance = ObjectDef(nodes = Seq(n),
          name = name)

        val result = instance.replaceEmpty(s, injector)

        result must beLike {
          case ObjectDef(n2, _) => {
            n2 must beLike {
              case Seq(nSeq) => nSeq must beAnInstanceOf[FunctionM]
            }
            n mustEqual name
          }
        }
      }

      "getMaxDepth" in {
        "getMaxDepth returns 1 + child getMaxDepth" in {
          val f = mock[FunctionM]
          f.getMaxDepth returns 2
          val objectM = ObjectDef(Seq(f), name)

          objectM.getMaxDepth mustEqual 3
        }

        "getMaxDepth returns 1 + child getMaxDepth" in {
          val f = mock[FunctionM]
          f.getMaxDepth returns 1
          val f2 = mock[FunctionM]
          f2.getMaxDepth returns 2
          val objectM = ObjectDef(Seq(f, f2), name)

          objectM.getMaxDepth mustEqual 3
        }
      }
    }
  }
}