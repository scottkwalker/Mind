package models.domain.scala

import composition.UnitTestHelpers
import models.common.IScope

final class ValueRefSpec extends UnitTestHelpers {

  "toCompilable" must {
    "return name" in {
      val valueRef = build()

      val compilable = valueRef.toCompilable

      compilable must equal(defaultName)
    }
  }

  "hasNoEmptySteps" must {
    "return true if it has a non-empty name and height is 0" in {
      val scope = mock[IScope]
      val valueRef = build()

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "return true if the name is not empty" in {
      val scope = mock[IScope]
      val valueRef = build()

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(true)
    }

    "return false if the name is empty" in {
      val scope = mock[IScope]
      val valueRef = build(name = "")

      val hasNoEmptySteps = valueRef.hasNoEmptySteps(scope)

      hasNoEmptySteps must equal(false)
    }
  }

  "fillEmptySteps" must {
    "return the same if there are no empty nodes" in {
      val scope = mock[IScope]
      val factoryLookup = mock[FactoryLookup]
      val valueRef = build()

      val step = valueRef.fillEmptySteps(scope, factoryLookup)

      whenReady(step) {
        _ must equal(valueRef)
      }(config = patienceConfig)
    }
  }

  "height" must {
    "returns 1" in {
      val valueRef = build()

      val height = valueRef.height

      height must equal(1)
    }
  }

  private val defaultName = "a"

  private def build(name: String = defaultName, scope: IScope = mock[IScope]) = ValueRefImpl(name)
}