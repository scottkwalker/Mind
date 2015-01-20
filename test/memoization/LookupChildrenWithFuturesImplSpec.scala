package memoization

import com.google.inject.AbstractModule
import composition.StubFactoryIdToFactory._
import composition.{StubRepository, StubFactoryIdToFactory, TestComposition}
import models.common.Scope
import models.domain.scala.FactoryLookup
import org.mockito.Matchers.any
import org.mockito.Mockito._
import replaceEmpty.ReplaceEmpty

final class LookupChildrenWithFuturesImplSpec extends TestComposition {

  "fetch with neighbours" must {
    "does not call FactoryIdToFactory.convert with ReplaceEmpty as the repository already contains the ids" in {
      val (lookupChildren, scope, factoryIdToFactory) = build

      val result = lookupChildren.fetch(scope = scope, childrenToChooseFrom = Set(fakeFactoryTerminates1Id))

      whenReady(result, browserTimeout) { _ => verify(factoryIdToFactory, never).convert(any[ReplaceEmpty])}
    }

    "call FactoryIdToFactory.convert(factory) for only the nodes that can terminate" in {
      val (lookupChildren, scope, factoryIdToFactory) = build

      val result = lookupChildren.fetch(scope = scope, childrenToChooseFrom = Set(fakeFactoryTerminates1Id))

      whenReady(result, browserTimeout) { _ => verify(factoryIdToFactory, times(2)).convert(fakeFactoryTerminates1Id)}
    }

    "return only the factories of nodes that can terminate" in {
      val (lookupChildren, scope, _) = build

      val result = lookupChildren.fetch(scope = scope,
        childrenToChooseFrom = Set(fakeFactoryDoesNotTerminateId,
          fakeFactoryTerminates1Id,
          fakeFactoryDoesNotTerminateId,
          fakeFactoryTerminates2Id)
      )

      whenReady(result, browserTimeout) {
        _ must equal(Set(fakeFactoryTerminates1, fakeFactoryTerminates2))
      }
    }
  }

  "fetch with current node" must {
    "call FactoryIdToFactory.convert(id) for only the nodes that can terminate" in {
      val (lookupChildren, scope, factoryIdToFactory) = build

      val result = lookupChildren.fetch(scope = scope, parent = fakeFactoryHasChildrenId)

      whenReady(result, browserTimeout) { _ =>
        verify(factoryIdToFactory, times(1)).convert(fakeFactoryHasChildrenId)
        verify(factoryIdToFactory, times(1)).convert(fakeFactoryTerminates1Id)
      }
    }

    "return only the ids of nodes that can terminate" in {
      val (lookupChildren, scope, _) = build

      val result = lookupChildren.fetch(scope = scope, parent = fakeFactoryHasChildrenId)

      whenReady(result, browserTimeout) {
        _ must equal(Set(fakeFactoryTerminates1Id, fakeFactoryTerminates2Id))
      }
    }
  }

  "size" must {
    "calls repository.size" in {
      val (lookupChildren, _, _, repositoryWithFutures) = buildWithStubbedRepository
      lookupChildren.size
      verify(repositoryWithFutures, times(1)).size
    }

    "return 0 when empty" in {
      val (lookupChildren, _, _) = build
      lookupChildren.size must equal(0)
    }

    "return 2 when repository has 2 items" in {
      val (lookupChildren, scope, _) = build
      val result = lookupChildren.fetch(scope = scope, parent = fakeFactoryHasChildrenId)
      whenReady(result, browserTimeout) { r =>
        lookupChildren.size must equal(2)
      }
    }
  }

  "sizeOfCalculated" must {
    "calls repository.sizeOfCalculated" in {
      val (lookupChildren, _, _, repositoryWithFutures) = buildWithStubbedRepository
      lookupChildren.sizeOfCalculated
      verify(repositoryWithFutures, times(1)).sizeOfCalculated
    }
  }

  private def build = {
    val scope = Scope(height = 1, maxHeight = 1)
    val factoryIdToFactory = mock[FactoryLookup]
    val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory)) // Override an implementation returned by IoC with a stubbed version.
    (injector.getInstance(classOf[LookupChildren]), scope, factoryIdToFactory)
  }

  private def buildWithStubbedRepository = {
    val scope = Scope(height = 1, maxHeight = 1)
    val factoryIdToFactory = mock[FactoryLookup]
    val repositoryWithFutures = mock[RepositoryWithFutures]
    val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory), new StubRepository(repositoryWithFutures)) // Override an implementation returned by IoC with a stubbed version.
    (injector.getInstance(classOf[LookupChildren]), scope, factoryIdToFactory, injector.getInstance(classOf[RepositoryWithFutures]))
  }
}