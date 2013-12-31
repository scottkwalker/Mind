package nodes.helpers

import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar
import com.google.inject.{Guice, Injector}
import ai.helpers.TestAiModule
import nodes.{AddOperatorFactory, IntegerMFactory}

class MemoizeSpec extends WordSpec with EasyMockSugar {
  "Memoize" should {
    "memoize recursive functions" in {
      val fib: Int => Int = {
        def fibRec(f: Int => Int)(n: Int): Int = {
          if (n == 0) 1
          else if (n == 1) 1
          else f(n - 1) + f(n - 2)
        }
        Memoize.Y(fibRec)
      }

      assert(fib(0) == 1)
      assert(fib(1) == 1)
      assert(fib(5) == 8)
    }

    "call the func that calculates the result only once" in {
      val scope = strictMock[IScope]

      trait ICalc {
        def doCalc: Boolean
      }
      val calc = strictMock[ICalc]
      val memoizeDi = MemoizeDi[IScope, Boolean]()

      expecting {
        (calc.doCalc andReturn true).once()
      }

      whenExecuting(scope, calc) {
        memoizeDi.store getOrElseUpdate(scope, calc.doCalc)
        memoizeDi.store getOrElseUpdate(scope, calc.doCalc)
      }
    }

    "IoC two different factory instances of the canTerminateInStepsRemaining map when two node factories are created" in {
      val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)
      val factory1 = injector.getInstance(classOf[IntegerMFactory])
      val factory2 = injector.getInstance(classOf[AddOperatorFactory])
      val scope = Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1)

      factory1.canTerminateInStepsRemaining(scope)

      assert(factory1.mapOfCanTerminateInStepsRemaining.store != factory2.mapOfCanTerminateInStepsRemaining.store)
    }

    "IoC two different factory instances of the legalNeighbours map when two node factories are created" in {
      val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)
      val factory1 = injector.getInstance(classOf[IntegerMFactory])
      val factory2 = injector.getInstance(classOf[AddOperatorFactory])
      val scope = Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1)

      factory1.legalNeighbours(scope)

      assert(factory1.mapOfLegalNeigbours.store != factory2.mapOfLegalNeigbours.store)
    }
  }
}
