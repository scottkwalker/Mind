package nodes

import models.common.IScope
import models.domain.scala.IntegerM
import modules.ai.legalGamer.LegalGamerModule
import composition.TestComposition

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

  override lazy val injector = testInjector(new LegalGamerModule)
  private val factory = injector.getInstance(classOf[IntegerMFactoryImpl])
}