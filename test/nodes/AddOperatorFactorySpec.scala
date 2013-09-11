package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule
import ai.helpers.TestAiModule

class AddOperatorFactorySpec extends Specification with Mockito {
  "AddOperatorFactory" should {
    "create returns instance of this type" in {
      val s = Scope(maxDepth = 10)
      val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)
      val factory = injector.getInstance(classOf[AddOperatorFactory])

      val instance = factory.create(scope = s)
      
      instance must beAnInstanceOf[AddOperator]
    }
  }
}