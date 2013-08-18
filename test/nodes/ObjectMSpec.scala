package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class ObjectMSpec extends Specification with Mockito {
  "ObjectM" should {
    "validate" in {
      "true given it can terminates in under N steps" in {
        val f = mock[FunctionM]
        f.validate(anyInt) returns true 
        val objectM = ObjectM(Seq(f))

        objectM.validate(4) mustEqual true
      }

      "false given it cannot terminate in 0 steps" in {
        val f = mock[FunctionM]
        f.validate(anyInt) throws new RuntimeException
        val objectM = ObjectM(Seq(f))

        objectM.validate(0) mustEqual false
      }
            
      "false given it cannot terminate in under N steps" in {
        val f = mock[FunctionM]
        f.validate(anyInt) returns false
        val objectM = ObjectM(Seq(f))

        objectM.validate(3) mustEqual false
      }

      "true given no empty nodes" in {
        val f = mock[FunctionM]
        f.validate(anyInt) returns true 
        val objectM = ObjectM(Seq(f))

        objectM.validate(10) mustEqual true
      }

      "false given single empty method node" in {
        val objectM = ObjectM(Seq(Empty()))
        objectM.validate(10) mustEqual false
      }

      "false given empty method node in a sequence" in {
        val f = mock[FunctionM]
        f.validate(anyInt) returns true
        val objectM = ObjectM(Seq(f, Empty()))
        
        objectM.validate(10) mustEqual false
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