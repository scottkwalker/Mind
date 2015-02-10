package models.domain.scala

import com.google.inject.Injector
import composition.TestComposition
import models.common.IScope

final class IntegerMSpec extends TestComposition {

  "toCompilable" must {
    "return expected" in {
      integerM.toCompilable must equal("Int")
    }
  }

  "hasNoEmptySteps" must {
    "returns true" in {
      val s = mock[IScope]
      integerM.hasNoEmptySteps(s) must equal(true)
    }
  }

  "fillEmptySteps" must {
    "return same when no empty nodes" in {
      val s = mock[IScope]
      val factoryLookup = mock[FactoryLookup]

      val result = integerM.fillEmptySteps(s)(factoryLookup)

      whenReady(result) {
        _ must equal(integerM)
      }(config = patienceConfig)
    }
  }

  "height" must {
    "return 1" in {
      integerM.height must equal(1)
    }
  }

  private val integerM = IntegerM()
}