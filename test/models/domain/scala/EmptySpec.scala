package models.domain.scala

import composition.TestHelpers
import models.common.IScope
import models.common.Scope

final class EmptySpec extends TestHelpers {

  "toCompilable" must {
    "throw if you ask toRawScala" in {
      an[RuntimeException] must be thrownBy Empty().toCompilable
    }
  }

  "hasNoEmptySteps" must {
    "return false" in {
      val scope = Scope(height = 10, maxHeight = 10)

      val hasNoEmptySteps = Empty().hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }
  }

  "fillEmptySteps" must {
    "throws" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val step = Empty()

      a[RuntimeException] must be thrownBy step.fillEmptySteps(scope, factoryLookup)
    }
  }

  "height" must {
    "return 0" in {
      val height = Empty().height
      height must equal(0)
    }
  }
}