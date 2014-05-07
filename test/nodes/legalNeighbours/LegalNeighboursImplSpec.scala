package nodes.legalNeighbours

import org.scalatest.{Matchers, WordSpec}
import nodes.helpers._

class LegalNeighboursImplSpec extends WordSpec with Matchers {
  "fetchLegalNeighbours" should {
    case class mockFactoryDoesNotTerminate() extends CreateChildNodesImpl with UpdateScopeNoChange {
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = false

      val neighbours: Seq[ICreateChildNodes] = Seq.empty
    }

    case class mockFactoryTerminates1() extends CreateChildNodesImpl with UpdateScopeNoChange {
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = true

      val neighbours: Seq[ICreateChildNodes] = Seq.empty
    }

    case class mockFactoryTerminates2() extends CreateChildNodesImpl with UpdateScopeNoChange {
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = true

      val neighbours: Seq[ICreateChildNodes] = Seq.empty
    }

    "returns only the neighbours that can terminate" in {
      val nNot = mockFactoryDoesNotTerminate()
      val n1 = mockFactoryTerminates1()
      val n2 = mockFactoryTerminates2()
      val scope = Scope(maxDepth = 10)
      val sut = new LegalNeighboursImpl(neighbours = Seq(nNot,
        n1,
        nNot,
        n2))
      sut.fetchLegalNeighbours(scope) should equal(Seq(n1, n2))
    }
  }
}
