package nodes.helpers

import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar
import com.google.inject.Guice
import nodes.IntegerMFactory
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule

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
      trait IFib { def fibRec(n: Int): Int }
      val mockFib = strictMock[IFib]
      val fib: Int => Int = {
        def fibRec(f: Int => Int)(n: Int): Int = mockFib.fibRec(n)
        Memoize.Y(fibRec)
      }

      expecting {
        (mockFib.fibRec(0) andReturn 1).once()
      }

      whenExecuting(mockFib) {
        fib(0)
        fib(0)
      }
    }
/*
    "IoC two different factory instances of the canTerminateInStepsRemaining map when two node factories are created" in {
      val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
      val factory1 = injector.getInstance(classOf[IntegerMFactory])
      val factory2 = injector.getInstance(classOf[AddOperatorFactory])
      val scope = Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1)

      factory1.canTerminateInStepsRemaining(scope)

      assert(factory1.mapOfCanTerminateInStepsRemaining != factory2.mapOfCanTerminateInStepsRemaining)
    }

    "IoC two different factory instances of the legalNeighbours map when two node factories are created" in {
      val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
      val factory1 = injector.getInstance(classOf[IntegerMFactory])
      val factory2 = injector.getInstance(classOf[AddOperatorFactory])
      val scope = Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1)

      factory1.legalNeighbours(scope)

      assert(factory1.mapOfLegalNeigbours != factory2.mapOfLegalNeigbours)
    }*/
  }
}
