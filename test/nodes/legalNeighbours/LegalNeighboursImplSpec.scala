package nodes.legalNeighbours

import org.scalatest.{Matchers, WordSpec}
import nodes.helpers._
import org.specs2.mock.Mockito

class LegalNeighboursImplSpec extends WordSpec with Matchers with Mockito {
  "fetchLegalNeighbours" should {
    val legalNeighboursImpl = new LegalNeighboursImpl

    case class FakeFactoryDoesNotTerminate() extends CreateChildNodesImpl with UpdateScopeNoChange {
      val nodeFactoryDoesNotTerminate = mock[ICreateChildNodes]
      nodeFactoryDoesNotTerminate.neighbours returns Seq(nodeFactoryDoesNotTerminate)
      nodeFactoryDoesNotTerminate.legalNeighbours returns legalNeighboursImpl

      val legalNeighbours: LegalNeighbours = legalNeighboursImpl

      val neighbours: Seq[ICreateChildNodes] = Seq(nodeFactoryDoesNotTerminate)
    }

    case class FakeFactoryTerminates1() extends CreateChildNodesImpl with UpdateScopeNoChange {
      val legalNeighbours: LegalNeighbours = legalNeighboursImpl

      val neighbours: Seq[ICreateChildNodes] = Seq.empty
    }

    case class FakeFactoryTerminates2() extends CreateChildNodesImpl with UpdateScopeNoChange {
      val legalNeighbours: LegalNeighbours = legalNeighboursImpl

      val neighbours: Seq[ICreateChildNodes] = Seq.empty
    }

    "returns only the neighbours that can terminate" in {
      val nNot = FakeFactoryDoesNotTerminate()
      val n1 = FakeFactoryTerminates1()
      val n2 = FakeFactoryTerminates2()
      val scope = Scope(maxDepth = 3)
      val sut = new LegalNeighboursImpl()

      val result = sut.fetch(scope = scope,
        neighbours = Seq(nNot,
          n1,
          nNot,
          n2))

      result should equal(Seq(n1, n2))
    }
  }
}
