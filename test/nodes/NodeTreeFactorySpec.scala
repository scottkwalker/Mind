package nodes

import org.specs2.mutable.Specification
import com.twitter.util.Eval
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule
import ai.aco.AcoModule

class NodeTreeFactorySpec extends Specification with Mockito with PendingUntilFixed {
  "NodeTreeFactory" should {
      val injector: Injector = Guice.createInjector(new DevModule, new AcoModule)
      val factory = injector.getInstance(classOf[NodeTreeFactory])
      
    "create returns instance of this type" in {
      val s = Scope(stepsRemaining = 10)

      val instance = factory.create(scope = s) 

      instance must beAnInstanceOf[NodeTree]
    }
  }
}