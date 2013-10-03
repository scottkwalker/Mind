package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.Injector

class ObjectMSpec extends Specification with Mockito {
  "ObjectM" should {
    val name = "o0"
    "validate" in {
      "true given it can terminates in under N steps" in {
        val s = Scope(maxDepth = 4)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns true 
        val objectM = ObjectM(Seq(f), name)

        objectM.validate(s) mustEqual true
      }

      "false given it cannot terminate in 0 steps" in {
        val s = Scope(depth = 0)
        val f = mock[FunctionM]
        f.validate(any[Scope]) throws new RuntimeException
        val objectM = ObjectM(Seq(f), name)

        objectM.validate(s) mustEqual false
      }
            
      "false given it cannot terminate in under N steps" in {
        val s = Scope(depth = 3)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns false
        val objectM = ObjectM(Seq(f), name)

        objectM.validate(s) mustEqual false
      }

      "true given no empty nodes" in {
        val s = Scope(maxDepth = 10)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns true 
        val objectM = ObjectM(Seq(f), name)

        objectM.validate(s) mustEqual true
      }

      "false given single empty method node" in {
        val s = Scope(maxDepth = 10)
        val objectM = ObjectM(Seq(Empty()), name)
        objectM.validate(s) mustEqual false
      }

      "false given empty method node in a sequence" in {
        val s = Scope(maxDepth = 10)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns true
        val objectM = ObjectM(Seq(f, Empty()), name)
        
        objectM.validate(s) mustEqual false
      }
    }

    "toRawScala" in {
      val f = mock[FunctionM]
      f.toRawScala returns "STUB"
      val objectM = ObjectM(Seq(f), name)

      objectM.toRawScala mustEqual "object o0 { STUB }"
    }

    "replaceEmpty" in {
      "returns same when no empty nodes" in {
        val s = mock[Scope]
        val f = mock[FunctionM]
        val i = mock[Injector]
        val instance = ObjectM(Seq(f), name)

        instance.replaceEmpty(s, i) mustEqual instance
      }
    }
  }
}