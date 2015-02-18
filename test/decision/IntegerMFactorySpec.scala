package decision

import composition.IntegerMBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.domain.scala.IntegerM

final class IntegerMFactorySpec extends UnitTestHelpers with TestComposition {

  "neighbours" must {
    "have no possible children" in {
      integerMFactory.nodesToChooseFrom.size must equal(0)
    }
  }

  "create step" must {
    "return instance of this type" in {
      // Arrange
      val scope = mock[IScope]

      // Act
      val step = integerMFactory.createStep(scope)

      // Assert
      whenReady(step) {
        _ mustBe a[IntegerM]
      }(config = patienceConfig)
    }
  }

  private def integerMFactory = testInjector(new IntegerMBinding).getInstance(classOf[IntegerMFactory])
}