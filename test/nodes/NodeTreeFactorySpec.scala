package nodes

import org.specs2.mutable.Specification
import com.twitter.util.Eval
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule

class NodeTreeFactorySpec extends Specification with Mockito with PendingUntilFixed {
  "NodeTreeFactory" should {
    "create returns instance of this type" in {
      val s = Scope(stepsRemaining = 10)
      val injector: Injector = Guice.createInjector(new DevModule)
      val factory = injector.getInstance(classOf[NodeTreeFactory])

      val instance = factory.create(scope = s) 

      instance must beAnInstanceOf[NodeTree]
    }
  }
}