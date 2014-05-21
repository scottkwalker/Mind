package nodes

import nodes.helpers.IScope
import com.google.inject.Injector
import models.domain.scala.IntegerM
import utils.helpers.UnitSpec

final class IntegerMSpec extends UnitSpec {
  "toRawScala" should {
    "return expected" in {
      IntegerM().toRaw should equal("Int")
    }
  }

  "validate" should {
    "returns true" in {
      val s = mock[IScope]
      IntegerM().validate(s) should equal(true)
    }
  }

  "replaceEmpty" should {
    "return same when no empty nodes" in {
      val s = mock[IScope]
      val i = mock[Injector]
      val instance = IntegerM()

      instance.replaceEmpty(s, i) should equal(instance)
    }
  }

  "getMaxDepth" should {
    "return 1" in {
      IntegerM().getMaxDepth should equal(1)
    }
  }
}