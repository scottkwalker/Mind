package nodes

import com.google.inject.{Guice, Injector}
import models.domain.scala.IntegerM
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import nodes.helpers._
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

  private val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
  private val factory = injector.getInstance(classOf[IntegerMFactoryImpl])
}