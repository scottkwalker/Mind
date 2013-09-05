package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class FunctionMSpec extends Specification with Mockito {
  "Function" should {
    val name = "f0"

    "validate" in {
      "false given an empty name" in {
        val s = Scope(stepsRemaining = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true
        FunctionM(nodes = Seq(v, v), name = "").validate(s) mustEqual false
      }

      "true given it can terminate in under N steps" in {
        val s = Scope(stepsRemaining = 3)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        FunctionM(Seq(v, v), name).validate(s) mustEqual true
      }

      "false given it cannot terminate in 0 steps" in {
        val s = Scope(stepsRemaining = 0)
        val v = mock[ValueRef]
        v.validate(any[Scope]) throws new RuntimeException

        FunctionM(Seq(v, v), name).validate(s) mustEqual false
      }

      "false given it cannot terminate in under N steps" in {
        val s = Scope(stepsRemaining = 2)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns false

        FunctionM(Seq(v, v), name).validate(s) mustEqual false
      }

      "true given no empty nodes" in {
        val s = Scope(stepsRemaining = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        FunctionM(Seq(v, v), name).validate(s) mustEqual true
      }

      "false given an empty node" in {
        val s = Scope(stepsRemaining = 10)
        val v = mock[ValueRef]
        v.validate(any[Scope]) returns true

        FunctionM(Seq(v, Empty()), name).validate(s) mustEqual false
      }
    }

    "toRawScala" in {
      val a = mock[ValueRef]
      a.toRawScala returns "STUB"

      FunctionM(Seq(a), name).toRawScala mustEqual "def f0(a: Int, b: Int) = { STUB }"
    }
  }
}