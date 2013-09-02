package nodes

import org.specs2.mutable._
import org.specs2.execute.PendingUntilFixed
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule
import ai.aco.AcoModule
import ai.helpers.TestAiModule

class FunctionMFactorySpec extends Specification with Mockito {
  "FunctionMFactory" should {
    val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)
    val factory = injector.getInstance(classOf[FunctionMFactory])
        
    "create" in {
      "returns instance of this type" in {
        val s = Scope(stepsRemaining = 10)
        
        val instance = factory.create(scope = s)

        instance must beAnInstanceOf[FunctionM]
      }

      "returns expected given scope with 0 functions" in {
        val s = Scope(numFuncs = 0, stepsRemaining = 10)
        
        val instance = factory.create(scope = s)

        instance must beLike {
          case FunctionM(_, name) => name mustEqual "f0"
        }
      }

      "returns expected given scope with 1 functions" in {
        val s = Scope(numFuncs = 1, stepsRemaining = 10)
        
        val instance = factory.create(scope = s)

        instance must beLike {
          case FunctionM(_, name) => name mustEqual "f1"
        }
      }
      
      "update scope calls increment functions" in {
        val s = mock[Scope]

        val instance = factory.updateScope(s)
        
        there was one(s).incrementFuncs
      }

      "returns 1 child given scope with 1 maxFuncs" in {
        val s = Scope(numFuncs = 0, stepsRemaining = 10, maxExpressionsInFunc = 1)

        val instance = factory.create(scope = s)

        instance must beLike {
          case FunctionM(child, _) => child.length mustEqual 1
        }
      }

      "returns 3 children given scope with 3 maxFuncs" in {
        val s = Scope(numFuncs = 0, stepsRemaining = 10, maxExpressionsInFunc = 3)

        val instance = factory.create(scope = s)

        instance must beLike {
          case FunctionM(child, _) => child.length mustEqual 3
        }
      }
    }
  }
}