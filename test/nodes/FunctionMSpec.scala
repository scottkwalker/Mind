package nodes

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.execute.PendingUntilFixed
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope

class FunctionMSpec extends Specification {
  "Function" should {
    "validate" in {
      "true given a non-empty name" in {
        FunctionM(nodes = Seq(ValueRef("a"), ValueRef("b")), name = "f0").validate(10) mustEqual true
      }

      "false given an empty name" in {
        FunctionM(nodes = Seq(ValueRef("a"), ValueRef("b")), name = "").validate(10) mustEqual false
      }

      "true given it can terminate in under N steps" in {
        FunctionM(Seq(AddOperator(ValueRef("a"), ValueRef("b")))).validate(3) mustEqual true
      }

      "false given it cannot terminate in under N steps" in {
        FunctionM(Seq(AddOperator(ValueRef("a"), ValueRef("b")))).validate(2) mustEqual false
      }

      "true given none empty" in {
        FunctionM(Seq(AddOperator(ValueRef("a"), ValueRef("b")))).validate(10) mustEqual true
      }

      "false given has an empty node" in {
        FunctionM(Seq(AddOperator(ValueRef("a"), Empty()))).validate(10) mustEqual false
      }
    }

    "toRawScala" in {
      FunctionM(Seq(AddOperator(ValueRef("a"), ValueRef("b")))).toRawScala mustEqual "def f0(a: Int, b: Int) = { a + b }"
    }

    "create" in {
      "returns instance of this type" in {
        FunctionM.create(scope = Scope()) must beAnInstanceOf[FunctionM]
      }

      "returns expected given scope with 0 functions" in {
        val scope = Scope()

        FunctionM.create(scope = scope) must beLike {
          case FunctionM(_, name) => name mustEqual "f0"
        }
      }

      "returns expected given scope with 1 functions" in {
        val scope = Scope().incrementFuncs

        FunctionM.create(scope = scope) must beLike {
          case FunctionM(_, name) => name mustEqual "f1"
        }
      }
    }
  }
}