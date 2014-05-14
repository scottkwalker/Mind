package nodes

import nodes.helpers.Scope
import com.google.inject.Injector
import models.domain.scala.Empty
import utils.helpers.UnitSpec

class EmptySpec extends UnitSpec {
  "toRaw" should {
    "throw if you ask toRawScala" in {
      an[RuntimeException] should be thrownBy Empty().toRaw
    }
  }

  "validate" should {
    "return false" in {
      val s = Scope(maxDepth = 10)
      Empty().validate(s) should equal(false)
    }
  }

  "replaceEmpty" should {
    "throws" in {
      val s = mock[Scope]
      val i = mock[Injector]

      val instance = Empty()

      an[RuntimeException] should be thrownBy instance.replaceEmpty(s, i)
    }
  }

  "getMaxDepth" should {
    "return 0" in {
      Empty().getMaxDepth should equal(0)
    }
  }
}