package nodes

import org.specs2.mutable._
import nodes.helpers.Scope
import org.specs2.mock.Mockito
import com.google.inject.Injector

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
      val i = mock[Injector]

      val instance = Empty()

      instance.replaceEmpty(s, i) must throwA[scala.RuntimeException]
    }
  }
}