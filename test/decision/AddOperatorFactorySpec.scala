package decision

import composition.TestComposition
import composition.DecisionBindings
import composition.UnitTestHelpers
import models.common.Scope
import models.domain.scala.AddOperator

final class AddOperatorFactorySpec extends UnitTestHelpers with TestComposition {

  "create step" must {
    "return instance of this type" in {
      val scope = Scope(height = 10, numVals = 1, maxHeight = 10)
      val addOperatorFactory = testInjector(new DecisionBindings).getInstance(classOf[AddOperatorFactory])

      val step = addOperatorFactory.createStep(scope = scope)

      whenReady(step) {
        _ mustBe a[AddOperator]
      }(config = patienceConfig)
    }
  }
}