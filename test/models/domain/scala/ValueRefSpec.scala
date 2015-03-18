package models.domain.scala

import composition.UnitTestHelpers
import models.common.IScope

final class ValueRefSpec extends UnitTestHelpers {

  "toCompilable" must {
    "return name" in {
      val name = "a"
      val valueRef = ValueRefImpl(name)

      val compilable = valueRef.toCompilable

      compilable must equal(name)
    }
  }

  "hasNoEmptySteps" must {
    "return true if it has a non-empty name and height is 0" in {
      val scope = mock[IScope]
      val name = "a"
      val valueRef = ValueRefImpl(name)

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "return true if the name is not empty" in {
      val scope = mock[IScope]
      val name = "a"
      val valueRef = ValueRefImpl(name)

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "return false if the name is empty" in {
      val scope = mock[IScope]
      val name = ""
      val valueRef = ValueRefImpl(name)

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }
  }

  "fillEmptySteps" must {
    "return the same if there are no empty nodes" in {
      val scope = mock[IScope]
      val name = "a"
      val factoryLookup = mock[FactoryLookup]
      val valueRef = ValueRefImpl(name)

      val step = valueRef.fillEmptySteps(scope, factoryLookup)

      whenReady(step) {
        _ must equal(valueRef)
      }(config = patienceConfig)
    }
  }

  "height" must {
    "returns 1" in {
      val name = "a"

      val height = ValueRefImpl(name).height

      height must equal(1)
    }
  }
}