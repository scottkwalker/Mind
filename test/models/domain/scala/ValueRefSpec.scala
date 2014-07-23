package models.domain.scala

import com.google.inject.Injector
import nodes.helpers.{IScope, Scope}
import utils.helpers.UnitSpec

final class ValueRefSpec extends UnitSpec {
  "toRawScala" should {
    "return name" in {
      val name = "a"

      ValueRef(name).toRaw should equal(name)
    }
  }

  "validate" should {
    "false given it cannot terminate in under N steps" in {
      val s = Scope(height = 0)
      val name = "a"

      ValueRef(name).validate(s) should equal(false)
    }

    "true given a non-empty name" in {
      val s = Scope(height = 10)
      val name = "a"

      ValueRef(name).validate(s) should equal(true)
    }

    "false given an empty name" in {
      val s = Scope(height = 10)
      val name = ""

      ValueRef(name).validate(s) should equal(false)
    }
  }

  "replaceEmpty" should {
    "returns same when no empty nodes" in {
      val s = mock[IScope]
      val name = "a"
      val injector = mock[Injector]
      val instance = ValueRef(name)

      instance.replaceEmpty(s, injector) should equal(instance)
    }
  }

  "getMaxDepth" should {
    "returns 1" in {
      val name = "a"

      ValueRef(name).getMaxDepth should equal(1)
    }
  }
}