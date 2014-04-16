package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import models.domain.scala.AddOperator

class AddOperatorFactorySpec extends Specification with Mockito {
  "AddOperatorFactory" should {
    "create returns instance of this type" in {
      val s = Scope(maxDepth = 10, numVals = 1)
      val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
      val factory = injector.getInstance(classOf[AddOperatorFactory])

      val instance = factory.create(scope = s)

      instance must beAnInstanceOf[AddOperator]
    }
  }
}