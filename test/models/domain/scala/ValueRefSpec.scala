package models.domain.scala

import composition.UnitTestHelpers
import models.common.IScope
import models.common.Scope

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
    "true when it has a non-empty name and height is 0" in {
      val scope = mock[IScope]
      val name = "a"
      val valueRef = ValueRefImpl(name)

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "true given a non-empty name" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val name = "a"
      val valueRef = ValueRefImpl(name)

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "false given an empty name" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val name = ""
      val valueRef = ValueRefImpl(name)

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }
  }

  "fillEmptySteps" must {
    "returns same when no empty nodes" in {
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