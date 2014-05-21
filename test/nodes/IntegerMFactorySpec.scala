package nodes

import nodes.helpers._
import com.google.inject.{Guice, Injector}
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule
import models.domain.scala.IntegerM
import utils.helpers.UnitSpec

final class IntegerMFactorySpec extends UnitSpec {
  "neighbours" should {
    "have no possible children" in {
      factory.neighbours.length should equal(0)
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
  private val factory = injector.getInstance(classOf[IntegerMFactory])
}