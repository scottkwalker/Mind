package nodes

import org.specs2.mutable._
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule

class FunctionMFactorySpec extends Specification with Mockito {
  "FunctionMFactory" should {
    "create" in {
      "returns instance of this type" in {
        val s = Scope(stepsRemaining = 10)
        val injector: Injector = Guice.createInjector(new DevModule)
        val factory = injector.getInstance(classOf[FunctionMFactory])

        val instance = factory.create(scope = s)

        instance must beAnInstanceOf[FunctionM]
      }

      "returns expected given scope with 0 functions" in {
        val s = Scope(numFuncs = 0, stepsRemaining = 10)
        val injector: Injector =  Guice.createInjector(new DevModule)

        FunctionMFactory(injector).create(scope = s) must beLike {
          case FunctionM(_, name) => name mustEqual "f0"
        }
      }

      "returns expected given scope with 1 functions" in {
        val s = Scope(numFuncs = 1, stepsRemaining = 10)
        val injector: Injector =  Guice.createInjector(new DevModule)

        FunctionMFactory(injector).create(scope = s) must beLike {
          case FunctionM(_, name) => name mustEqual "f1"
        }
      }
    }
  }
}