package nodes

import models.common.Scope
import models.domain.scala.AddOperator
import composition.TestComposition

final class AddOperatorFactorySpec extends TestComposition {

  "create" must {
    "return instance of this type" in {
      val s = Scope(height = 10, numVals = 1)
      val factory = injector.getInstance(classOf[AddOperatorFactoryImpl])

      val instance = factory.create(scope = s)

      instance mustBe a[AddOperator]
    }
  }
}