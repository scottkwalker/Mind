package models.domain.scala

import composition.TestComposition
import models.common.IScope
import models.common.Scope

final class ValueRefSpec extends TestComposition {

  "toCompilable" must {
    "return name" in {
      val name = "a"
      val valueRef = ValueRef(name)

      val compilable = valueRef.toCompilable

      compilable must equal(name)
    }
  }

  "hasNoEmptySteps" must {
    "true when it has a non-empty name and height is 0" in {
      val scope = Scope(height = 0)
      val name = "a"
      val valueRef = ValueRef(name)

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "true given a non-empty name" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val name = "a"
      val valueRef = ValueRef(name)

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "false given an empty name" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val name = ""
      val valueRef = ValueRef(name)

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }
  }

  "fillEmptySteps" must {
    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      val name = "a"
      val factoryLookup = mock[FactoryLookup]
      val valueRef = ValueRef(name)

      val step = valueRef.fillEmptySteps(scope, factoryLookup)

      whenReady(step) {
        _ must equal(valueRef)
      }(config = patienceConfig)
    }
  }

  "height" must {
    "returns 1" in {
      val name = "a"

      val height = ValueRef(name).height

      height must equal(1)
    }
  }
}