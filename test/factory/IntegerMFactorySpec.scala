package factory

import composition.TestComposition
import models.common.IScope
import models.domain.scala.IntegerM
import modules.ai.legalGamer.LegalGamerModule

final class IntegerMFactorySpec extends TestComposition {

  "neighbours" must {
    "have no possible children" in {
      factory.neighbourIds.length must equal(0)
    }
  }

  "create" must {
    "return instance of this type" in {
      // Arrange
      val s = mock[IScope]

      // Act
      val instance = factory.create(s)

      // Assert
      instance mustBe a[IntegerM]
    }
  }

  private val injector = testInjector()
  private val factory = injector.getInstance(classOf[IntegerMFactoryImpl])
}