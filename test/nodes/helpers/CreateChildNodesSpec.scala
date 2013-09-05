package nodes.helpers

import org.specs2.mutable._
import nodes._
import org.specs2.mock.Mockito
import ai.helpers.TestAi
import ai.Ai

class CreateChildNodesSpec extends Specification with Mockito {
  "CreateChildNodes" should {
    val mockFactoryNotTerminates1 = mock[CreateChildNodes]
    mockFactoryNotTerminates1.canTerminateInStepsRemaining(any[Scope]) returns false
    val mockFactoryTerminates1 = mock[CreateChildNodes]
    mockFactoryTerminates1.canTerminateInStepsRemaining(any[Scope]) returns true
    val mockFactoryTerminates2 = mock[CreateChildNodes]
    mockFactoryTerminates2.canTerminateInStepsRemaining(any[Scope]) returns true

    case class TestCreateChildNodes() extends CreateChildNodes {
      val neighbours: Seq[CreateChildNodes] = Seq(mockFactoryNotTerminates1, mockFactoryTerminates1, mockFactoryNotTerminates1, mockFactoryTerminates2)
      def create(scope: Scope): Node = Empty()
    }
    
    case class TestCreateChildNodesNoValidChildren() extends CreateChildNodes {
      val neighbours: Seq[CreateChildNodes] = Seq(mockFactoryNotTerminates1)
      def create(scope: Scope): Node = Empty()
    }

    "validChildren returns filtered seq" in {
      val scope = Scope(stepsRemaining = 10)
      val sut = TestCreateChildNodes()

      sut.legalNeighbours(scope) mustEqual Seq(mockFactoryTerminates1, mockFactoryTerminates2)
    }
    
    "couldTerminate returns true if any child node terminates" in {
      val scope = Scope(stepsRemaining = 10)
      val sut = TestCreateChildNodes()

      sut.canTerminateInStepsRemaining(scope) mustEqual true
    }

    "updateScope returns unchanged by default" in {
      val scope = Scope(stepsRemaining = 10)
      val sut = TestCreateChildNodes()

      sut.updateScope(scope) mustEqual scope
    }
  }
}