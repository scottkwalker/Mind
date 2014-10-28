package memoization

import composition.StubFactoryIdToFactory._
import composition.{StubFactoryIdToFactory, TestComposition}
import models.common.Scope
import org.mockito.Matchers.any
import org.mockito.Mockito.{never, times, verify}
import scala.concurrent.Await

final class LookupNeighboursImplSpec extends TestComposition {

  "fetch with neighbours" must {
    "does not call FactoryIdToFactory as the repository already contains the ids" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryLookup]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory)) // Override an implementation returned by IoC with a stubbed version.
      val legalNeighboursImpl = injector.getInstance(classOf[LookupNeighbours])

      legalNeighboursImpl.fetch(scope = scope, neighbours = Seq(fakeFactoryTerminates1Id))

      verify(factoryIdToFactory, never).convert(any[Int])
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

      whenReady(result) {
        _ must equal(Seq(fakeFactoryTerminates1, fakeFactoryTerminates2))
      }
    }
  }

  "fetch with current node" must {
    "call FactoryIdToFactory.convert(id) for only the nodes that can terminate" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryLookup]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory))
      val legalNeighboursImpl = injector.getInstance(classOf[LookupNeighbours])

      val result = Await.result(legalNeighboursImpl.fetch(scope = scope, currentNode = fakeFactoryHasChildrenId), finiteTimeout)

      verify(factoryIdToFactory, times(1)).convert(fakeFactoryHasChildrenId)
      verify(factoryIdToFactory, times(1)).convert(fakeFactoryTerminates1Id)
    }

    "return only the ids of nodes that can terminate" in {
      val scope = Scope(height = 3)
      val factoryIdToFactory = mock[FactoryLookup]
      val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory))
      val legalNeighboursImpl = injector.getInstance(classOf[LookupNeighbours])

      val result = Await.result(legalNeighboursImpl.fetch(scope = scope, currentNode = fakeFactoryHasChildrenId), finiteTimeout)

      result must equal(Seq(fakeFactoryTerminates1Id, fakeFactoryTerminates2Id))
    }
  }
}