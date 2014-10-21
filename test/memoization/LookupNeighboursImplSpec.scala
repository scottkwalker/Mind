package memoization

import composition.StubFactoryIdToFactory._
import composition.{StubFactoryIdToFactory, TestComposition}
import models.common.Scope
import org.mockito.Mockito.{times, verify}

final class LookupNeighboursImplSpec extends TestComposition {

  "fetch with neighbours" must {
    "call FactoryIdToFactory.convert(factory) for only the nodes that can terminate" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryLookup]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory)) // Override an implementation returned by IoC with a stubbed version.
      val legalNeighboursImpl = injector.getInstance(classOf[LookupNeighbours])

      legalNeighboursImpl.fetch(scope = scope, neighbours = Seq(fakeFactoryTerminates1Id))

      verify(factoryIdToFactory, times(2)).convert(fakeFactoryTerminates1Id)
    }

    "return only the factories of nodes that can terminate" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryLookup]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory)) // Override an implementation returned by IoC with a stubbed version.
      val legalNeighboursImpl = injector.getInstance(classOf[LookupNeighbours])

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
      val legalNeighboursImpl = injector.getInstance(classOf[LookupNeighbours])

      legalNeighboursImpl.fetch(scope = scope, currentNode = fakeFactoryHasChildrenId)

      verify(factoryIdToFactory, times(1)).convert(fakeFactoryTerminates1)
      verify(factoryIdToFactory, times(1)).convert(fakeFactoryTerminates2)
    }

    "return only the ids of nodes that can terminate" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryLookup]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory))
      val legalNeighboursImpl = injector.getInstance(classOf[LookupNeighbours])

      val result = legalNeighboursImpl.fetch(scope = scope, currentNode = fakeFactoryHasChildrenId)

      result must equal(Seq(fakeFactoryTerminates1Id, fakeFactoryTerminates2Id))
    }
  }
}