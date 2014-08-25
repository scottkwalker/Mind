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

  "validate" must {
    "false given it cannot terminate in under N steps" in {
      val s = Scope(height = 0)
      val name = "a"

      ValueRef(name).validate(s) must equal(false)
    }

    "true given a non-empty name" in {
      val s = Scope(height = 10)
      val name = "a"

      ValueRef(name).validate(s) must equal(true)
    }

    "false given an empty name" in {
      val s = Scope(height = 10)
      val name = ""

      ValueRef(name).validate(s) must equal(false)
    }
  }

  "replaceEmpty" must {
    "returns same when no empty nodes" in {
      val s = mock[IScope]
      val name = "a"
      implicit val injector = mock[Injector]
      val instance = ValueRef(name)

      instance.replaceEmpty(s) must equal(instance)
    }
  }

  "getMaxDepth" must {
    "returns 1" in {
      val name = "a"

      ValueRef(name).getMaxDepth must equal(1)
    }
  }
}