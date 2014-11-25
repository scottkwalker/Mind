package models.domain.scala

import com.google.inject.Injector
import composition.TestComposition
import models.common.{IScope, Scope}

final class ValueRefSpec extends TestComposition {

  "toRawScala" must {
    "return name" in {
      val name = "a"

      ValueRef(name).toRaw must equal(name)
    }
  }

  "hasNoEmpty" must {
    "true when it has a non-empty name and height is 0" in {
      val s = Scope(height = 0)
      val name = "a"

      ValueRef(name).hasNoEmpty(s) must equal(true)
    }

    "true given a non-empty name" in {
      val s = Scope(height = 10)
      val name = "a"

      ValueRef(name).hasNoEmpty(s) must equal(true)
    }

    "false given an empty name" in {
      val s = Scope(height = 10)
      val name = ""

      ValueRef(name).hasNoEmpty(s) must equal(false)
    }
  }

  "replaceEmpty" must {
    "returns same when no empty nodes" in {
      val s = mock[IScope]
      val name = "a"
      val i = mock[Injector]
      val instance = ValueRef(name)

      val result = instance.replaceEmpty(s)(i)

      whenReady(result) {
        _ must equal(instance)
      }
    }
  }

  "height" must {
    "returns 1" in {
      val name = "a"

      ValueRef(name).height must equal(1)
    }
  }
}