package nodes.legalNeighbours

import com.tzavellas.sse.guice.ScalaModule
import models.common.Scope
import nodes.helpers._
import org.mockito.Mockito._
import utils.helpers.UnitSpec

final class LegalNeighboursMemoImplSpec extends UnitSpec {

  private val fT1 = {
    val factory = mock[ICreateChildNodes]
    when(factory.neighbourIds).thenReturn(Seq.empty)
    factory
  }
  private val fT2 = {
    val factory = mock[ICreateChildNodes]
    when(factory.neighbourIds).thenReturn(Seq.empty)
    factory
  }

  private val fakeFactoryDoesNotTerminateId = 0

  private val fakeFactoryTerminates1Id = 1

  private val fakeFactoryTerminates2Id = 2

  private final class StubFactoryIdToFactory extends ScalaModule {

    def configure(): Unit = {
      val fNot: ICreateChildNodes = {
        val factory = mock[ICreateChildNodes]
        when(factory.neighbourIds).thenReturn(Seq(fakeFactoryDoesNotTerminateId))
        factory
      }

      val factoryIdToFactory = mock[FactoryIdToFactory]
      when(factoryIdToFactory.convert(fakeFactoryDoesNotTerminateId)).thenReturn(fNot)
      when(factoryIdToFactory.convert(fakeFactoryTerminates1Id)).thenReturn(fT1)
      when(factoryIdToFactory.convert(fakeFactoryTerminates2Id)).thenReturn(fT2)
      bind(classOf[FactoryIdToFactory]).toInstance(factoryIdToFactory)
    }
  }

  "fetch with neighbours" should {
    "call FactoryIdToFactory.convert(factory)" in pending

    "returns only the neighbours that can terminate" in {
      val scope = Scope(height = 3)
      val injector = testInjector(new StubFactoryIdToFactory) // Override an implementation returned by IoC with a stubbed version.
      val legalNeighboursImpl = injector.getInstance(classOf[LegalNeighboursMemo])

      val result = legalNeighboursImpl.fetch(scope = scope,
        neighbours = Seq(fakeFactoryDoesNotTerminateId,
          fakeFactoryTerminates1Id,
          fakeFactoryDoesNotTerminateId,
          fakeFactoryTerminates2Id)
      )

      result should equal(Seq(fT1, fT2))
    }
  }

  "fetch with current node" should {
    "call FactoryIdToFactory.convert(id)" in pending
    "returns only the neighbours that can terminate" in pending
  }
}
