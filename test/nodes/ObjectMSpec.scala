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
    
    "create" in {
      "returns instance of this type" in {
        val s = mock[Scope]
        s.numFuncs returns 0
        s.numObjects returns 0
        s.numVals returns 0
        
        ObjectM.create(scope = s) must beAnInstanceOf[ObjectM]
      }
      
      "returns expected given scope with 0 functions" in {
        val s = mock[Scope]
        s.numObjects returns 0

        ObjectM.create(scope = s) must beLike {
          case ObjectM(_, name) => name mustEqual "o0"
        }
      }

      "returns expected given scope with 1 functions" in {
        val s = mock[Scope]
        s.numObjects returns 1

        ObjectM.create(scope = s) must beLike {
          case ObjectM(_, name) => name mustEqual "o1"
        }
      }
    }
          
  }
}