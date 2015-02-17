package memoization

import com.google.inject.Key
import composition._
import models.common.IScope
import models.common.Scope
import utils.PozInt

import scala.concurrent.Future

final class LookupChildrenWithFuturesImplSpec extends TestHelpers with TestComposition {

  //  "fetch with neighbours" must {
  //    "does not call factoryLookup.convert with ReplaceEmpty as the repository already contains the ids" in {
  //      val (lookupChildren, scope, factoryLookup) = build()
  //
  //      val result = lookupChildren.get(scope = scope, childrenToChooseFrom = Set(fakeFactoryTerminates1Id))
  //
  //      whenReady(result) { _ => verify(factoryLookup, never).convert(any[Decision])}(config = patienceConfig)
  //    }
  //
  //    "call factoryLookup.convert(factory) for only the nodes that can terminate" in {
  //      val (lookupChildren, scope, factoryLookup) = build()
  //
  //      val result = lookupChildren.get(scope = scope, childrenToChooseFrom = Set(fakeFactoryTerminates1Id))
  //
  //      whenReady(result) { _ => verify(factoryLookup, times(1)).convert(fakeFactoryTerminates1Id)}(config = patienceConfig)
  //    }
  //
  //    "return only the factories of nodes that can terminate" in {
  //      val (lookupChildren, scope, _) = build()
  //
  //      val result = lookupChildren.get(scope = scope,
  //        childrenToChooseFrom = Set(
  //          fakeFactoryDoesNotTerminateId,
  //          fakeFactoryTerminates1Id,
  //          fakeFactoryTerminates2Id
  //        )
  //      )
  //
  //      whenReady(result) {
  //        _ must equal(Set(fakeFactoryTerminates1, fakeFactoryTerminates2))
  //      }(config = patienceConfig)
  //    }
  //  }

  //  "fetch with current node" must {
  //    "call factoryLookup.convert(id) with the id of the parent" in {
  //      val (lookupChildren, scope, factoryLookup) = build()
  //
  //      val result = lookupChildren.get(scope = scope, parent = fakeFactoryHasChildrenId)
  //
  //      whenReady(result) { _ =>
  //        verify(factoryLookup, times(1)).convert(fakeFactoryHasChildrenId)
  //      }(config = patienceConfig)
  //    }
  //
  //    "return only the ids of nodes that can terminate" in {
  //      val (lookupChildren, scope, _) = build()
  //
  //      val result = lookupChildren.get(scope = scope, parent = fakeFactoryHasChildrenId)
  //
  //      whenReady(result) {
  //        _ must equal(Set(fakeFactoryTerminates1Id, fakeFactoryTerminates2Id))
  //      }(config = patienceConfig)
  //    }
  //  }
  //
  //  "size" must {
  //    "calls repository.size" in {
  //      val (lookupChildren, _, _, repositoryWithFutures) = buildWithStubbedRepository
  //      lookupChildren.size
  //      verify(repositoryWithFutures, times(1)).size
  //    }
  //
  //    "return 0 when empty" in {
  //      val (lookupChildren, _, _) = build()
  //      lookupChildren.size must equal(0)
  //    }
  //
  //    "return 2 when repository has 2 items" in {
  //      val (lookupChildren, scope, _) = build(size = 2)
  //      val result = lookupChildren.get(scope = scope, parent = fakeFactoryHasChildrenId)
  //      whenReady(result) { r =>
  //        lookupChildren.size must equal(2)
  //      }
  //    }
  //  }

  private def build(size: Int = 0) = {
    val scope = Scope(height = 1, maxHeight = 1)
    val factoryLookup = new StubFactoryLookupBinding
    val injector = testInjector(
      factoryLookup, // Override an implementation returned by IoC with a stubbed version.
      new StubRepositoryWithFuture(size = size),
      new LookupChildrenWithFuturesBinding
    )
    (injector.getInstance(classOf[LookupChildrenWithFutures]), scope, factoryLookup.stub)
  }

  private def buildWithStubbedRepository = {
    val scope = Scope(height = 1, maxHeight = 1)
    val factoryLookup = new StubFactoryLookupBinding
    val injector = testInjector(
      factoryLookup, // Override an implementation returned by IoC with a stubbed version.
      new LookupChildrenWithFuturesBinding
    )
    (injector.getInstance(classOf[LookupChildrenWithFutures]), scope, factoryLookup.stub, injector.getInstance(new Key[Memoize2[IScope, PozInt, Future[Boolean]]]() {}))
  }

  private def buildWithRealRepository = {
    val scope = Scope(height = 1, maxHeight = 1)
    val factoryLookup = new StubFactoryLookupBinding
    val injector = testInjector(
      factoryLookup, // Override an implementation returned by IoC with a stubbed version.
      new RepositoryWithFuturesBinding,
      new LookupChildrenWithFuturesBinding
    )
    (injector.getInstance(classOf[LookupChildrenWithFutures]), scope, factoryLookup.stub)
  }
}