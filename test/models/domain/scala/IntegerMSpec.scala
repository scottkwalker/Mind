package models.domain.scala

import com.google.inject.Injector
import composition.TestComposition
import models.common.IScope

final class IntegerMSpec extends TestComposition {

  "toRawScala" must {
    "return expected" in {
      integerM.toRaw must equal("Int")
    }
  }

  "hasNoEmpty" must {
    "returns true" in {
      val s = mock[IScope]
      integerM.hasNoEmpty(s) must equal(true)
    }
  }

  "replaceEmpty" must {
    "return same when no empty nodes" in {
      val s = mock[IScope]
      val injector = mock[Injector]

      val result = integerM.replaceEmpty(s)(injector)

      whenReady(result) {
        _ must equal(integerM)
      }(config = whenReadyPatienceConfig)
    }
  }

  "height" must {
    "return 1" in {
      integerM.height must equal(1)
    }
  }

  private val integerM = IntegerM()
}