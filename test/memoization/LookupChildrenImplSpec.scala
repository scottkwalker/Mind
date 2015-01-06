package memoization

import composition.StubFactoryIdToFactory._
import composition.{StubFactoryIdToFactory, TestComposition}
import models.common.Scope
import models.domain.scala.FactoryLookup
import org.mockito.Matchers.any
import org.mockito.Mockito.{never, times, verify}
import replaceEmpty.ReplaceEmpty

final class LookupChildrenImplSpec extends TestComposition {

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

  private def build = {
    val scope = Scope(height = 3)
    val factoryIdToFactory = mock[FactoryLookup]
    val injector = testInjector(new StubFactoryIdToFactory(factoryIdToFactory)) // Override an implementation returned by IoC with a stubbed version.
    (injector.getInstance(classOf[LookupChildren]), scope, factoryIdToFactory)
  }
}