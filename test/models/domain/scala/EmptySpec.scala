package models.domain.scala

import composition.UnitTestHelpers
import utils.ScopeHelper._

final class EmptySpec extends UnitTestHelpers {

  "toCompilable" must {
    "throw an exception" in {
      an[RuntimeException] must be thrownBy Empty().toCompilable
    }
  }

  "hasNoEmptySteps" must {
    "return false" in {
      val hasNoEmptySteps = Empty().hasNoEmptySteps(scope())
      hasNoEmptySteps must equal(false)
    }
  }

  "fillEmptySteps" must {
    "throw an exception" in {
      val factoryLookup = mock[FactoryLookup]
      val step = Empty()

      a[RuntimeException] must be thrownBy step.fillEmptySteps(scope(), factoryLookup)
    }
  }

  "height" must {
    "return 0" in {
      val height = Empty().height
      height must equal(0)
    }
  }
}