package nodes.legalNeighbours

import nodes.helpers._
import models.domain.common.Node
import utils.helpers.UnitSpec
import org.mockito.Mockito._

final class LegalNeighboursImplSpec extends UnitSpec {
  private val fNot: ICreateChildNodes = {
    val factory = mock[ICreateChildNodes]
    when(factory.neighbourIds).thenReturn(Seq(FakeFactoryDoesNotTerminate.id))
    factory
  }
  private val fT1 = FakeFactoryTerminates1()
  private val fT2 = FakeFactoryTerminates2()
  private val legalNeighboursImpl = {
    implicit val factoryIdToFactory = mock[FactoryIdToFactory]
    when(factoryIdToFactory.convert(FakeFactoryDoesNotTerminate.id)).thenReturn(fNot)
    when(factoryIdToFactory.convert(FakeFactoryTerminates1.id)).thenReturn(fT1)
    when(factoryIdToFactory.convert(FakeFactoryTerminates2.id)).thenReturn(fT2)
    new LegalNeighboursImpl
  }

  case class FakeFactoryDoesNotTerminate() extends ICreateChildNodes with UpdateScopeNoChange {
    override val neighbourIds: Seq[Int] = Seq(FakeFactoryDoesNotTerminate.id)

    override def create(scope: IScope): Node = ???
  }

  object FakeFactoryDoesNotTerminate {
    val id = 0
  }

  case class FakeFactoryTerminates1() extends ICreateChildNodes with UpdateScopeNoChange {
    override val neighbourIds: Seq[Int] = Seq.empty

    override def create(scope: IScope): Node = ???
  }

  object FakeFactoryTerminates1 {
    val id = 1
  }

  case class FakeFactoryTerminates2() extends ICreateChildNodes with UpdateScopeNoChange {
    override val neighbourIds: Seq[Int] = Seq.empty

    override def create(scope: IScope): Node = ???
  }

  object FakeFactoryTerminates2 {
    val id = 2
  }

  "fetchLegalNeighbours" should {
    "returns only the neighbours that can terminate" in {
      val scope = Scope(depth = 3)

      val result = legalNeighboursImpl.fetch(scope = scope,
        neighbours = Seq(FakeFactoryDoesNotTerminate.id,
          FakeFactoryTerminates1.id,
          FakeFactoryDoesNotTerminate.id,
          FakeFactoryTerminates2.id)
      )

      result should equal(Seq(fT1, fT2))
    }
  }

}
