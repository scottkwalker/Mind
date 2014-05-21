package nodes

import nodes.helpers._
import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar
import com.google.inject.{Guice, Injector}
import org.scalatest.Matchers._
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule
import models.domain.scala.IntegerM
import utils.helpers.UnitSpec

class IntegerMFactorySpec extends UnitSpec {
  val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
  val factory = injector.getInstance(classOf[IntegerMFactory])

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
}