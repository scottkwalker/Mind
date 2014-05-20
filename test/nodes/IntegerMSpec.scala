package nodes

import org.specs2.mutable._
import nodes.helpers.{IScope, Scope}
import org.specs2.mock.Mockito
import com.google.inject.Injector
import models.domain.scala.IntegerM

class IntegerMSpec extends Specification with Mockito {
  "toRawScala" should {
    "return expected" in {
      IntegerM().toRaw mustEqual "Int"
    }
  }

  "validate" should {
    "returns true" in {
      val s = mock[IScope]
      IntegerM().validate(s) mustEqual true
    }
  }

  "replaceEmpty" should {
    "return same when no empty nodes" in {
      val s = mock[IScope]
      val i = mock[Injector]
      val instance = IntegerM()

      instance.replaceEmpty(s, i) mustEqual instance
    }
  }

  "getMaxDepth" should {
    "return 1" in {
      IntegerM().getMaxDepth mustEqual 1
    }
  }
}