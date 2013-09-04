package nodes.helpers

import org.specs2.mutable._
import nodes.helpers._

class MemoizeSpec extends Specification {
  "Memoize" should {
    "memoize recursive functions" in {
      val fib: BigInt => BigInt = {
        def fibRec(f: BigInt => BigInt)(n: BigInt): BigInt = {
          if (n == 0) 1
          else if (n == 1) 1
          else (f(n-1) + f(n-2))
        }
        Memoize.Y(fibRec)
      }

      fib(0) must equalTo(1)
      fib(1) must equalTo(1)
      fib(5) must equalTo(8)
    }
  }
}
