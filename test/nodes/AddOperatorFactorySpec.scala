package nodes

import models.common.Scope
import models.domain.scala.AddOperator
import utils.helpers.UnitSpec

final class AddOperatorFactorySpec extends UnitSpec {

  "create" should {
    "return instance of this type" in {
      val s = Scope(height = 10, numVals = 1)
      val factory = injector.getInstance(classOf[AddOperatorFactoryImpl])

      val instance = factory.create(scope = s)

      instance shouldBe a[AddOperator]
    }
  }
}