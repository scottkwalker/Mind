package replaceEmpty

import composition.TestComposition
import models.common.IScope
import models.domain.scala.IntegerM

final class IntegerMFactorySpec extends TestComposition {

  "neighbours" must {
    "have no possible children" in {
      integerMFactory.nodesToChooseFrom.length must equal(0)
    }
  }

  "create" must {
    "return instance of this type" in {
      // Arrange
      val scope = mock[IScope]

      // Act
      val instruction = integerMFactory.create(scope)

      // Assert
      whenReady(instruction) { result =>
        result mustBe a[IntegerM]
      }
    }
  }

  private val integerMFactory = testInjector().getInstance(classOf[IntegerMFactoryImpl])
}