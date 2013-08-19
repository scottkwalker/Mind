package nodes

import org.specs2.mutable._
import nodes.helpers.Scope

class EmptySpec extends Specification {
  "Empty" should {
    "throw if you ask toRawScala" in {
      Empty().toRawScala must throwA[scala.RuntimeException]
    }

    "validate false" in {
      val s = Scope(stepsRemaining = 10)
      Empty().validate(s) mustEqual false
    }
  }
}