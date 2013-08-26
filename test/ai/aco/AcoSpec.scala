package ai.aco

import org.specs2.mutable._
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Guice
import ai.helpers.TestAiModule
import nodes.helpers.CreateChildNodes
import org.specs2.mock.Mockito

class AcoSpec extends Specification with Mockito {
  "Aco" should {
    "chooseChild returns expected instance" in {
      val sut = Aco()
      val v = mock[CreateChildNodes]
      val possibleChildren = Seq(v)
      
      sut.chooseChild(possibleChildren) must beAnInstanceOf[CreateChildNodes]
    }
  }
}