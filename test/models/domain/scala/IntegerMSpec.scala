package models.domain.scala

import com.google.inject.Injector
import composition.TestComposition
import models.common.IScope

final class IntegerMSpec extends TestComposition {

  "toRawScala" must {
    "return expected" in {
      IntegerM().toRaw must equal("Int")
    }
  }

  "hasNoEmpty" must {
    "returns true" in {
      val s = mock[IScope]
      IntegerM().hasNoEmpty(s) must equal(true)
    }
  }

  "replaceEmpty" must {
    "return same when no empty nodes" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]
      val instance = IntegerM()

      val result = instance.replaceEmpty(s)

      whenReady(result) {
        _ must equal(instance)
      }
    }
  }

  "height" must {
    "return 1" in {
      IntegerM().height must equal(1)
    }
  }
}