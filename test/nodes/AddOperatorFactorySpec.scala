package nodes

import com.google.inject.{Guice, Injector}
import models.domain.scala.AddOperator
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import nodes.helpers.Scope
import utils.helpers.UnitSpec

final class AddOperatorFactorySpec extends UnitSpec {

  "create" should {
    "return instance of this type" in {
      val s = Scope(height = 10, numVals = 1)
      val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
      val factory = injector.getInstance(classOf[AddOperatorFactoryImpl])

      val instance = factory.create(scope = s)

      instance shouldBe a[AddOperator]
    }
  }
}