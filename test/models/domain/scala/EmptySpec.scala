package models.domain.scala

import com.google.inject.Injector
import composition.TestComposition
import models.common.{IScope, Scope}

final class EmptySpec extends TestComposition {

  "toRaw" must {
    "throw if you ask toRawScala" in {
      an[RuntimeException] must be thrownBy Empty().toRaw
    }
  }

  "validate" must {
    "return false" in {
      val s = Scope(height = 10)
      Empty().validate(s) must equal(false)
    }
  }

  "replaceEmpty" must {
    "throws" in {
      val s = mock[IScope]
      implicit val i = mock[Injector]

      val instance = Empty()

      a[RuntimeException] must be thrownBy instance.replaceEmpty(s)
    }
  }

  "getMaxDepth" must {
    "return 0" in {
      Empty().getMaxDepth must equal(0)
    }
  }
}