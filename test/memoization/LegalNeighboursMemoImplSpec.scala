package memoization

import com.google.inject.AbstractModule
import composition.TestComposition
import factory.ReplaceEmpty
import models.common.Scope
import org.mockito.Mockito.{times, verify, when}

final class LegalNeighboursMemoImplSpec extends TestComposition {

  "fetch with neighbours" must {
    "call FactoryIdToFactory.convert(factory) for only the nodes that can terminate" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryLookup]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory)) // Override an implementation returned by IoC with a stubbed version.
      val legalNeighboursImpl = injector.getInstance(classOf[LegalNeighboursMemo])

      legalNeighboursImpl.fetch(scope = scope, neighbours = Seq(fakeFactoryTerminates1Id))

      verify(factoryIdToFactory, times(2)).convert(fakeFactoryTerminates1Id)
    }

    "return only the factories of nodes that can terminate" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryLookup]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory)) // Override an implementation returned by IoC with a stubbed version.
      val legalNeighboursImpl = injector.getInstance(classOf[LegalNeighboursMemo])

      val result = legalNeighboursImpl.fetch(scope = scope,
        neighbours = Seq(fakeFactoryDoesNotTerminateId,
          fakeFactoryTerminates1Id,
          fakeFactoryDoesNotTerminateId,
          fakeFactoryTerminates2Id)
      )

      result must equal(Seq(fakeFactoryTerminates1, fakeFactoryTerminates2))
    }
  }

  "fetch with current node" must {
    "call FactoryIdToFactory.convert(id) for only the nodes that can terminate" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryLookup]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory))
      val legalNeighboursImpl = injector.getInstance(classOf[LegalNeighboursMemo])

      legalNeighboursImpl.fetch(scope = scope, currentNode = fakeFactoryHasChildrenId)

      verify(factoryIdToFactory, times(1)).convert(fakeFactoryTerminates1)
      verify(factoryIdToFactory, times(1)).convert(fakeFactoryTerminates2)
    }

    "return only the ids of nodes that can terminate" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryLookup]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory))
      val legalNeighboursImpl = injector.getInstance(classOf[LegalNeighboursMemo])

      val result = legalNeighboursImpl.fetch(scope = scope, currentNode = fakeFactoryHasChildrenId)

      result must equal(Seq(fakeFactoryTerminates1Id, fakeFactoryTerminates2Id))
    }
  }

  private val fakeFactoryTerminates1 = {
    val factory = mock[ReplaceEmpty]
    when(factory.neighbourIds).thenReturn(Seq.empty)
    factory
  }
  private val fakeFactoryTerminates2 = {
    val factory = mock[ReplaceEmpty]
    when(factory.neighbourIds).thenReturn(Seq.empty)
    factory
  }

  private val fakeFactoryDoesNotTerminateId = 0
  private val fakeFactoryTerminates1Id = 1
  private val fakeFactoryTerminates2Id = 2
  private val fakeFactoryHasChildrenId = 3

  private final class StubFactoryIdToFactory(factoryIdToFactory: FactoryLookup) extends AbstractModule {

    def configure(): Unit = {
      val fNot: ReplaceEmpty = {
        val fakeFactoryDoesNotTerminate = mock[ReplaceEmpty]
        when(fakeFactoryDoesNotTerminate.neighbourIds).thenReturn(Seq(fakeFactoryDoesNotTerminateId))
        fakeFactoryDoesNotTerminate
      }
      val fakeFactoryHasChildren: ReplaceEmpty = {
        val fakeFactoryDoesNotTerminate = mock[ReplaceEmpty]
        when(fakeFactoryDoesNotTerminate.neighbourIds).thenReturn(Seq(fakeFactoryTerminates1Id, fakeFactoryTerminates2Id))
        fakeFactoryDoesNotTerminate
      }
      // Id -> factory
      when(factoryIdToFactory.convert(fakeFactoryDoesNotTerminateId)).thenReturn(fNot)
      when(factoryIdToFactory.convert(fakeFactoryTerminates1Id)).thenReturn(fakeFactoryTerminates1)
      when(factoryIdToFactory.convert(fakeFactoryTerminates2Id)).thenReturn(fakeFactoryTerminates2)
      when(factoryIdToFactory.convert(fakeFactoryHasChildrenId)).thenReturn(fakeFactoryHasChildren)
      // Factory -> Id
      when(factoryIdToFactory.convert(fakeFactoryTerminates1)).thenReturn(fakeFactoryTerminates1Id)
      when(factoryIdToFactory.convert(fakeFactoryTerminates2)).thenReturn(fakeFactoryTerminates2Id)

      bind(classOf[FactoryLookup]).toInstance(factoryIdToFactory)
    }
  }

}