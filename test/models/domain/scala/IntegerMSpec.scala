package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import utils.helpers.UnitSpec2

final class IntegerMSpec extends UnitSpec2 {

  "toRawScala" must {
    "return expected" in {
      IntegerM().toRaw must equal("Int")
    }
  }

  "validate" must {
    "returns true" in {
      val s = mock[IScope]
      IntegerM().validate(s) must equal(true)
    }
  }

  "replaceEmpty" must {
    "return same when no empty nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val instance = IntegerM()

      instance.replaceEmpty(s) must equal(instance)
    }
  }

  "getMaxDepth" must {
    "return 1" in {
      IntegerM().getMaxDepth must equal(1)
    }
  }
}