package nodes.helpers

import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar

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
  }
}
