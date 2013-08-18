package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule

class AddOperatorFactorySpec extends Specification with Mockito {
  "AddOperatorFactory" should {
    "create returns instance of this type" in {
      val s = mock[Scope]
      s.numVals returns 0
      val injector: Injector = Guice.createInjector(new DevModule)
      val factory = injector.getInstance(classOf[AddOperatorFactory])

      val instance = factory.create(scope = s)
      
      instance must beAnInstanceOf[AddOperator]
    }
  }
}