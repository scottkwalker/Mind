package nodes

import org.specs2.mutable._
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class FunctionMSpec extends Specification with Mockito {
  "Function" should {
    "validate" in {
      "true given a non-empty name" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns true

        // TODO name should be generated from Scope
        FunctionM(nodes = Seq(v, v), name = "f0").validate(10) mustEqual true
      }

      "false given an empty name" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns true

        FunctionM(nodes = Seq(v, v), name = "").validate(10) mustEqual false
      }

      "true given it can terminate in under N steps" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns true

        FunctionM(Seq(v, v)).validate(3) mustEqual true
      }

      "false given it cannot terminate in 0 steps" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns false

        FunctionM(Seq(v, v)).validate(0) mustEqual false
      }

      "false given it cannot terminate in under N steps" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns false

        FunctionM(Seq(v, v)).validate(2) mustEqual false
      }

      "true given no empty nodes" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns true

        FunctionM(Seq(v, v)).validate(10) mustEqual true
      }

      "false given an empty node" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns true

        FunctionM(Seq(v, Empty())).validate(10) mustEqual false
      }
    }

    "toRawScala" in {
      val a = mock[ValueRef]
      a.toRawScala returns "a + b"

      FunctionM(Seq(a)).toRawScala mustEqual "def f0(a: Int, b: Int) = { a + b }"
    }

    "create" in {
      "returns instance of this type" in {
        val s = mock[Scope]
        s.numFuncs returns 0

        FunctionM.create(scope = Scope()) must beAnInstanceOf[FunctionM]
      }

      "returns expected given scope with 0 functions" in {
        val s = mock[Scope]
        s.numFuncs returns 0

        FunctionM.create(scope = s) must beLike {
          case FunctionM(_, name) => name mustEqual "f0"
        }
      }

      "returns expected given scope with 1 functions" in {
        val s = mock[Scope]
        s.numFuncs returns 1

        FunctionM.create(scope = s) must beLike {
          case FunctionM(_, name) => name mustEqual "f1"
        }
      }
    }
  }
}