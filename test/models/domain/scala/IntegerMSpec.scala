package models.domain.scala

import composition.TestHelpers
import models.common.IScope

final class IntegerMSpec extends TestHelpers {

  "toCompilable" must {
    "return expected" in {
      val compilable = integerM.toCompilable
      compilable must equal("Int")
    }
  }

  "hasNoEmptySteps" must {
    "returns true" in {
      val s = mock[IScope]
      val hasNoEmptySteps = integerM.hasNoEmptySteps(s)
      hasNoEmptySteps must equal(true)
    }
  }

  "fillEmptySteps" must {
    "return same when no empty nodes" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]

      val result = integerM.fillEmptySteps(scope, factoryLookup)

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