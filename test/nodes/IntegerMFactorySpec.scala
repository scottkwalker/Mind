package nodes

import models.common.IScope
import models.domain.scala.IntegerM
import modules.ai.legalGamer.LegalGamerModule
import utils.helpers.UnitSpec

final class IntegerMFactorySpec extends UnitSpec {

  "neighbours" should {
    "have no possible children" in {
      factory.neighbourIds.length should equal(0)
    }
  }

  "create" should {
    "return instance of this type" in {
      // Arrange
      val s = mock[IScope]

      // Act
      val instance = factory.create(s)

      // Assert
      instance shouldBe a[IntegerM]
    }
  }

  override lazy val injector = testInjector(new LegalGamerModule)
  private val factory = injector.getInstance(classOf[IntegerMFactoryImpl])
}