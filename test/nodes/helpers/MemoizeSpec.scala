package nodes.helpers

import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar

class MemoizeSpec extends WordSpec with EasyMockSugar {
  "Memoize" should {
    "call the func that calculates the result only once" in {
      val scope = strictMock[IScope]

      trait ICalc {
        def doCalc: Boolean
      }
      val calc = strictMock[ICalc]
      val memoizeDi = MemoizeDi()

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
