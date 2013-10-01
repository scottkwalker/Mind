package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito

class EmptySpec extends Specification with Mockito {
  "Empty" should {
    "throw if you ask toRawScala" in {
      Empty().toRawScala must throwA[scala.RuntimeException]
    }

    "validate false" in {
      val s = Scope(maxDepth = 10)
      Empty().validate(s) mustEqual false
    }

    "replaceEmpty throws" in {
      val s = mock[Scope]
      val instance = Empty()

      instance.replaceEmpty(s) must throwA[scala.RuntimeException]
    }
  }
}