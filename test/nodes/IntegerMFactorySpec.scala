package nodes

import nodes.helpers.{DevModule, Scope}
import org.specs2.mutable._
import org.specs2.mock.Mockito
import com.google.inject.{Guice, Injector}
import ai.helpers.TestAiModule

class IntegerMFactorySpec extends Specification with Mockito {
  "IntegerMFactory" should {
    val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)
    val factory = injector.getInstance(classOf[IntegerMFactory])

    "has no possible children" in {
      factory.neighbours.length mustEqual 0
    }

    "create" in {
      "returns instance of this type" in {
        val s = mock[Scope]

        val instance = factory.create(scope = s)

        instance must beAnInstanceOf[IntegerM]
      }
    }
  }
}