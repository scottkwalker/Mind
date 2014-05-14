package nodes.legalNeighbours

import nodes.helpers._
import models.domain.common.Node
import utils.helpers.UnitSpec
import org.mockito.Mockito._

class LegalNeighboursImplSpec extends UnitSpec {
  "fetchLegalNeighbours" should {
    val legalNeighboursImpl = new LegalNeighboursImpl

    case class FakeFactoryDoesNotTerminate() extends ICreateChildNodes with UpdateScopeNoChange {
      val nodeFactoryDoesNotTerminate = mock[ICreateChildNodes]
      when(nodeFactoryDoesNotTerminate.neighbours).thenReturn(Seq(nodeFactoryDoesNotTerminate))
      when(nodeFactoryDoesNotTerminate.legalNeighbours).thenReturn(legalNeighboursImpl)

      val legalNeighbours: LegalNeighbours = legalNeighboursImpl
      val neighbours: Seq[ICreateChildNodes] = Seq(nodeFactoryDoesNotTerminate)
      override def create(scope: IScope): Node = ???
    }

    case class FakeFactoryTerminates1() extends ICreateChildNodes with UpdateScopeNoChange {
      val legalNeighbours: LegalNeighbours = legalNeighboursImpl
      val neighbours: Seq[ICreateChildNodes] = Seq.empty
      override def create(scope: IScope): Node = ???
    }

    case class FakeFactoryTerminates2() extends ICreateChildNodes with UpdateScopeNoChange {
      val legalNeighbours: LegalNeighbours = legalNeighboursImpl
      val neighbours: Seq[ICreateChildNodes] = Seq.empty
      override def create(scope: IScope): Node = ???
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
