package models.domain.scala

import com.google.inject.Injector
import composition.TestComposition
import models.common.IScope
import models.common.Scope

final class EmptySpec extends TestComposition {

  "toRaw" must {
    "throw if you ask toRawScala" in {
      an[RuntimeException] must be thrownBy Empty().toRaw
    }
  }

  "hasNoEmpty" must {
    "return false" in {
      val scope = Scope(height = 10, maxHeight = 10)
      Empty().hasNoEmpty(scope) must equal(false)
    }
  }

  "fillEmptySteps" must {
    "throws" in {
      val scope = mock[IScope]
      implicit val injector = mock[Injector]

      val instance = Empty()

      a[RuntimeException] must be thrownBy instance.fillEmptySteps(scope)
    }
  }

  "height" must {
    "return 0" in {
      Empty().height must equal(0)
    }
  }
}