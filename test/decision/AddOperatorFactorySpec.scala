package decision

import composition.TestComposition
import models.common.Scope
import models.domain.scala.AddOperator

final class AddOperatorFactorySpec extends TestComposition {

  "create step" must {
    "return instance of this type" in {
      val scope = Scope(height = 10, numVals = 1, maxHeight = 10)
      val addOperatorFactory = testInjector().getInstance(classOf[AddOperatorFactoryImpl])

      val instruction = addOperatorFactory.createStep(scope = scope)

      whenReady(instruction) { result =>
        result mustBe a[AddOperator]
      }(config = patienceConfig)
    }
  }
}