package replaceEmpty

import composition.TestComposition
import models.common.IScope
import models.domain.scala.IntegerM

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
      whenReady(instance) { result =>
        result mustBe a[IntegerM]
      }
    }
  }

  private val factory = testInjector().getInstance(classOf[IntegerMFactoryImpl])
}