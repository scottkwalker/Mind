package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class ObjectMSpec extends Specification with Mockito {
  "ObjectM" should {
    "validate" in {
      "true given it can terminates in under N steps" in {
        val s = Scope(stepsRemaining = 4)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns true 
        val objectM = ObjectM(Seq(f))

        objectM.validate(s) mustEqual true
      }

      "false given it cannot terminate in 0 steps" in {
        val s = Scope(stepsRemaining = 0)
        val f = mock[FunctionM]
        f.validate(any[Scope]) throws new RuntimeException
        val objectM = ObjectM(Seq(f))

        objectM.validate(s) mustEqual false
      }
            
      "false given it cannot terminate in under N steps" in {
        val s = Scope(stepsRemaining = 3)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns false
        val objectM = ObjectM(Seq(f))

        objectM.validate(s) mustEqual false
      }

      "true given no empty nodes" in {
        val s = Scope(stepsRemaining = 10)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns true 
        val objectM = ObjectM(Seq(f))

        objectM.validate(s) mustEqual true
      }

      "false given single empty method node" in {
        val s = Scope(stepsRemaining = 10)
        val objectM = ObjectM(Seq(Empty()))
        objectM.validate(s) mustEqual false
      }

      "false given empty method node in a sequence" in {
        val s = Scope(stepsRemaining = 10)
        val f = mock[FunctionM]
        f.validate(any[Scope]) returns true
        val objectM = ObjectM(Seq(f, Empty()))
        
        objectM.validate(s) mustEqual false
      }
    }

    "toRawScala" in {
      val f = mock[FunctionM]
      f.toRawScala returns "STUB"
      val objectM = ObjectM(Seq(f))

      objectM.toRawScala mustEqual "object Individual { STUB }"
    }    
  }
}