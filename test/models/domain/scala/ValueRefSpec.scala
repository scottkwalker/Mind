package models.domain.scala

import com.google.inject.Injector
import composition.TestComposition
import models.common.IScope
import models.common.Scope

final class ValueRefSpec extends TestComposition {

  "toRawScala" must {
    "return name" in {
      val name = "a"

      ValueRef(name).toRaw must equal(name)
    }
  }

  "hasNoEmpty" must {
    "true when it has a non-empty name and height is 0" in {
      val scope = Scope(height = 0)
      val name = "a"

      ValueRef(name).hasNoEmpty(scope) must equal(true)
    }

    "true given a non-empty name" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val name = "a"

      ValueRef(name).hasNoEmpty(scope) must equal(true)
    }

    "false given an empty name" in {
      val scope = Scope(height = 10, maxHeight = 10)
      val name = ""

      ValueRef(name).hasNoEmpty(scope) must equal(false)
    }
  }

  "replaceEmpty" must {
    "returns same when no empty nodes" in {
      val scope = mock[IScope]
      val name = "a"
      val injector = mock[Injector]
      val valueRef = ValueRef(name)

      val result = valueRef.replaceEmpty(scope)(injector)

      whenReady(result) {
        _ must equal(valueRef)
      }(config = patienceConfig)
    }
  }

  "height" must {
    "returns 1" in {
      val name = "a"

      ValueRef(name).height must equal(1)
    }
  }
}