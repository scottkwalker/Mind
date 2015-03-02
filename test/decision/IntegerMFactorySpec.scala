package decision

import composition.IntegerMBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.domain.scala.IntegerM

final class IntegerMFactorySpec extends UnitTestHelpers with TestComposition {

  "neighbours" must {
    "have no possible children" in {
      val (integerMFactory, _) = build
      integerMFactory.nodesToChooseFrom.size must equal(0)
    }
  }

  "create step" must {
    "return instance of this type" in {
      val (integerMFactory, scope) = build

      val step = integerMFactory.createStep(scope)

      whenReady(step) {
        _ mustBe a[IntegerM]
      }(config = patienceConfig)
    }
  }

  "createParams" must {
    "throw exception" in {
      val (integerMFactory, scope) = build
      a[RuntimeException] must be thrownBy integerMFactory.createParams(scope).futureValue
    }
  }

  "createNodes" must {
    "throw exception" in {
      val (integerMFactory, scope) = build
      a[RuntimeException] must be thrownBy integerMFactory.createNodes(scope).futureValue
    }
  }

  private def build = {
    val scope = mock[IScope]
    val integerMFactory = testInjector(new IntegerMBinding).getInstance(classOf[IntegerMFactory])
    (integerMFactory, scope)
  }
}