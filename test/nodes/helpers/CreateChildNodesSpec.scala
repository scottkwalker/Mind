package nodes.helpers

import org.specs2.mutable._
import nodes._
import org.specs2.mock.Mockito
import ai.helpers.TestAi

class CreateChildNodesSpec extends Specification with Mockito {
  "CreateChildNodes" should {
    val mockFactoryNotTerminates1 = mock[CreateChildNodes]
    mockFactoryNotTerminates1.couldTerminate(any[Scope]) returns false
    val mockFactoryTerminates1 = mock[CreateChildNodes]
    mockFactoryTerminates1.couldTerminate(any[Scope]) returns true
    val mockFactoryTerminates2 = mock[CreateChildNodes]
    mockFactoryTerminates2.couldTerminate(any[Scope]) returns true

    case class TestCreateChildNodes() extends CreateChildNodes {
      val allPossibleChildren: Seq[CreateChildNodes] = Seq(mockFactoryNotTerminates1, mockFactoryTerminates1, mockFactoryNotTerminates1, mockFactoryTerminates2)
      def create(scope: Scope): Node = Empty()
    }

    "validChildren returns filtered seq" in {
      val scope = Scope(stepsRemaining = 10)
      val sut = TestCreateChildNodes()

      val v = sut.validChildren(scope)

      v mustEqual Seq(mockFactoryTerminates1, mockFactoryTerminates2)
    }
    
    "couldTerminate returns true if any child node terminates" in {
      val scope = Scope(stepsRemaining = 10)
      val sut = TestCreateChildNodes()

      sut.couldTerminate(scope) mustEqual true
    }

    "chooseChild returns expected" in {
      val scope = Scope(stepsRemaining = 10)
      val ai = TestAI()
      val sut = TestCreateChildNodes()

      sut.chooseChild(scope, ai) mustEqual mockFactoryTerminates1
    }
  }
}