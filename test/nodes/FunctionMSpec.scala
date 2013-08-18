package nodes

import org.specs2.mutable._
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class FunctionMSpec extends Specification with Mockito {
  "Function" should {
    "validate" in {
      "false given an empty name" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns true
// TODO use scope to create function name.
        FunctionM(nodes = Seq(v, v), name = "").validate(10) mustEqual false
      }

      "true given it can terminate in under N steps" in {
        val v = mock[ValueRef]
        v.validate(anyInt) returns true

        FunctionM(Seq(v, v)).validate(3) mustEqual true
      }

      "false given it cannot terminate in 0 steps" in {
        val v = mock[ValueRef]
        v.validate(anyInt) throws new RuntimeException

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
      a.toRawScala returns "STUB"

      FunctionM(Seq(a)).toRawScala mustEqual "def f0(a: Int, b: Int) = { STUB }"
    }
  }
}