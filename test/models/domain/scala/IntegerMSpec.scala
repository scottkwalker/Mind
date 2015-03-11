package models.domain.scala

import composition.UnitTestHelpers
import models.common.IScope
import utils.ScopeHelper._

final class IntegerMSpec extends UnitTestHelpers {

  "toCompilable" must {
    "return expected" in {
      val compilable = integerM.toCompilable
      compilable must equal("Int")
    }
  }

  "hasNoEmptySteps" must {
    "returns true" in {
      val hasNoEmptySteps = integerM.hasNoEmptySteps(scope())
      hasNoEmptySteps must equal(true)
    }
  }

  "fillEmptySteps" must {
    "return same when no empty nodes" in {
      val factoryLookup = mock[FactoryLookup]

      val nonEmptyInteger = integerM.fillEmptySteps(scope(), factoryLookup)

      whenReady(nonEmptyInteger) {
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