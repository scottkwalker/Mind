package nodes.legalNeighbours

import com.tzavellas.sse.guice.ScalaModule
import models.common.Scope
import nodes.helpers._
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import utils.helpers.UnitSpec

final class LegalNeighboursMemoImplSpec extends UnitSpec {

  private val fakeFactoryDoesNotTerminate1 = {
    val factory = mock[ICreateChildNodes]
    when(factory.neighbourIds).thenReturn(Seq.empty)
    factory
  }
  private val fakeFactoryDoesNotTerminate2 = {
    val factory = mock[ICreateChildNodes]
    when(factory.neighbourIds).thenReturn(Seq.empty)
    factory
  }

  private val fakeFactoryDoesNotTerminateId = 0

  private val fakeFactoryTerminates1Id = 1

  private val fakeFactoryTerminates2Id = 2

  private final class StubFactoryIdToFactory(factoryIdToFactory: FactoryIdToFactory) extends ScalaModule {

    def configure(): Unit = {
      val fNot: ICreateChildNodes = {
        val fakeFactoryDoesNotTerminate = mock[ICreateChildNodes]
        when(fakeFactoryDoesNotTerminate.neighbourIds).thenReturn(Seq(fakeFactoryDoesNotTerminateId))
        fakeFactoryDoesNotTerminate
      }
      when(factoryIdToFactory.convert(fakeFactoryDoesNotTerminateId)).thenReturn(fNot)
      when(factoryIdToFactory.convert(fakeFactoryTerminates1Id)).thenReturn(fakeFactoryDoesNotTerminate1)
      when(factoryIdToFactory.convert(fakeFactoryTerminates2Id)).thenReturn(fakeFactoryDoesNotTerminate2)
      bind(classOf[FactoryIdToFactory]).toInstance(factoryIdToFactory)
    }
  }

  "fetch with neighbours" should {
    "call FactoryIdToFactory.convert(factory)" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryIdToFactory]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory)) // Override an implementation returned by IoC with a stubbed version.
      val legalNeighboursImpl = injector.getInstance(classOf[LegalNeighboursMemo])

      legalNeighboursImpl.fetch(scope = scope, neighbours = Seq(fakeFactoryTerminates1Id))

      verify(factoryIdToFactory, times(2)).convert(fakeFactoryTerminates1Id)
    }

    "returns only the neighbours that can terminate" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryIdToFactory]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory)) // Override an implementation returned by IoC with a stubbed version.
      val legalNeighboursImpl = injector.getInstance(classOf[LegalNeighboursMemo])

      val result = legalNeighboursImpl.fetch(scope = scope,
        neighbours = Seq(fakeFactoryDoesNotTerminateId,
          fakeFactoryTerminates1Id,
          fakeFactoryDoesNotTerminateId,
          fakeFactoryTerminates2Id)
      )

      result should equal(Seq(fakeFactoryDoesNotTerminate1, fakeFactoryDoesNotTerminate2))
    }
  }

  "fetch with current node" should {
    "call FactoryIdToFactory.convert(id)" in pending
    "returns only the neighbours that can terminate" in pending
  }
}
