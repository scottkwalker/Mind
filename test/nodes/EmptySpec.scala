package nodes

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

class EmptySpec extends Specification {
  "Empty" should {
    "throw if you ask is terminal" in {
      Empty().isTerminal must throwA[scala.RuntimeException]
    }
    
    "throw if you ask canTerminate" in {
      Empty().canTerminate(1) must throwA[scala.RuntimeException]
    }
    
    "throw if you ask toRawScala" in {
      Empty().toRawScala must throwA[scala.RuntimeException]
    }
  }
}