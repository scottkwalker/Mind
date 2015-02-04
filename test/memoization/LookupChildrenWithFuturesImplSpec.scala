package memoization

import com.google.inject.Key
import composition.StubFactoryLookupBinding._
import composition.{LookupChildrenWithFuturesBinding, StubFactoryLookupBinding, StubRepositoryWithFuture, TestComposition}
import models.common.{IScope, Scope}
import org.mockito.Matchers.any
import org.mockito.Mockito._
import replaceEmpty.ReplaceEmpty
import utils.PozInt

import scala.concurrent.Future

final class LookupChildrenWithFuturesImplSpec extends TestComposition {

  "fetch with neighbours" must {
    "does not call FactoryIdToFactory.convert with ReplaceEmpty as the repository already contains the ids" in {
      val (lookupChildren, scope, factoryIdToFactory) = build

      val result = lookupChildren.get(scope = scope, childrenToChooseFrom = Set(fakeFactoryTerminates1Id))

      whenReady(result) { _ => verify(factoryIdToFactory, never).convert(any[ReplaceEmpty])}(config = patienceConfig)
    }

    "call FactoryIdToFactory.convert(factory) for only the nodes that can terminate" in {
      val (lookupChildren, scope, factoryIdToFactory) = build

      val result = lookupChildren.get(scope = scope, childrenToChooseFrom = Set(fakeFactoryTerminates1Id))

      whenReady(result) { _ => verify(factoryIdToFactory, times(2)).convert(fakeFactoryTerminates1Id)}(config = patienceConfig)
    }

    "return only the factories of nodes that can terminate" in {
      val (lookupChildren, scope, _) = build

      val result = lookupChildren.get(scope = scope,
        childrenToChooseFrom = Set(fakeFactoryDoesNotTerminateId,
          fakeFactoryTerminates1Id,
          fakeFactoryDoesNotTerminateId,
          fakeFactoryTerminates2Id)
      )

      whenReady(result) {
        _ must equal(Set(fakeFactoryTerminates1, fakeFactoryTerminates2))
      }(config = patienceConfig)
    }
  }

  "fetch with current node" must {
    "call FactoryIdToFactory.convert(id) for only the nodes that can terminate" in {
      val (lookupChildren, scope, factoryIdToFactory) = build

      val result = lookupChildren.get(scope = scope, parent = fakeFactoryHasChildrenId)

      whenReady(result) { _ =>
        verify(factoryIdToFactory, times(1)).convert(fakeFactoryHasChildrenId)
        verify(factoryIdToFactory, times(1)).convert(fakeFactoryTerminates1Id)
      }(config = patienceConfig)
    }

    "return only the ids of nodes that can terminate" in {
      val (lookupChildren, scope, _) = build

      val result = lookupChildren.get(scope = scope, parent = fakeFactoryHasChildrenId)

      whenReady(result) {
        _ must equal(Set(fakeFactoryTerminates1Id, fakeFactoryTerminates2Id))
      }(config = patienceConfig)
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
      val result = lookupChildren.get(scope = scope, parent = fakeFactoryHasChildrenId)
      whenReady(result) { r =>
        lookupChildren.size must equal(2)
      }
    }
  }

  private def build = {
    val scope = Scope(height = 1, maxHeight = 1)
    val factoryIdToFactory = new StubFactoryLookupBinding
    val injector = testInjector(
      factoryIdToFactory, // Override an implementation returned by IoC with a stubbed version.
      new LookupChildrenWithFuturesBinding
    )
    (injector.getInstance(classOf[LookupChildrenWithFutures]), scope, factoryIdToFactory.stub)
  }

  private def buildWithStubbedRepository = {
    val scope = Scope(height = 1, maxHeight = 1)
    val factoryIdToFactory = new StubFactoryLookupBinding
    val repositoryWithFutures = new StubRepositoryWithFuture
    val injector = testInjector(
      factoryIdToFactory, // Override an implementation returned by IoC with a stubbed version.
      repositoryWithFutures,
      new LookupChildrenWithFuturesBinding
    )
    (injector.getInstance(classOf[LookupChildrenWithFutures]), scope, factoryIdToFactory.stub, injector.getInstance(new Key[Memoize2[IScope, PozInt, Future[Boolean]]]() {}))
  }
}