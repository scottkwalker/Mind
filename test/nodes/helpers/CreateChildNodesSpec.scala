package nodes.helpers

import org.specs2.mutable._
import nodes._
import org.specs2.mock.Mockito

class CreateChildNodesSpec extends Specification with Mockito {
  "CreateChildNodes" should {
    val mockFactoryNotTerminate = mock[CreateChildNodes]
    mockFactoryNotTerminate.couldTerminate(any[Scope]) returns false
    val mockFactoryTerminates = mock[CreateChildNodes]
    mockFactoryTerminates.couldTerminate(any[Scope]) returns true

    case class TestCreateChildNodes() extends CreateChildNodes {
      val allPossibleChildren: Seq[CreateChildNodes] = Seq(mockFactoryNotTerminate, mockFactoryTerminates, mockFactoryNotTerminate)
      def create(scope: Scope): Node = Empty()
    }

    "validChildren returns filtered seq" in {
      val scope = Scope(stepsRemaining = 10)
      val v = TestCreateChildNodes().validChildren(scope)

      v mustEqual Seq(mockFactoryTerminates)
    }
  }
}