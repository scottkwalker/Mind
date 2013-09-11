package nodes

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule
import ai.helpers.TestAiModule

class NodeTreeFactorySpec extends Specification with Mockito with PendingUntilFixed {
  "NodeTreeFactory" should {
    val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)
    val factory = injector.getInstance(classOf[NodeTreeFactory])

    "create returns instance of this type" in {
      val s = Scope(maxDepth = 10)
      val instance = factory.create(s)

      instance must beAnInstanceOf[NodeTree]
    }
  }
}