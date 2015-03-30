package memoization

import composition.StubFactoryLookupAnyBinding.hasChildrenThatTerminateId
import composition.StubFactoryLookupAnyBinding.leaf2Id
import composition.StubFactoryLookupAnyBinding.leaf1Id
import composition._
import models.common.IScope
import org.mockito.Mockito._
import org.mockito.Mockito.times
import utils.PozInt

final class LookupChildrenSpec extends UnitTestHelpers with TestComposition {

  "size" must {
    "return repository size" in {
      val (lookupChildren, _, _, _) = build
      lookupChildren.size must equal(4)
    }
  }

  "get (scope, childrenToChooseFrom)" must {
    "call repository.contains once for each neighbour" in {
      val (lookupChildren, scope, _, repository) = build
      val neighbour0 = PozInt(0)
      val neighbour1 = PozInt(1)
      val neighbour2 = PozInt(2)
      val childrenToChooseFrom: Set[PozInt] = Set(neighbour0, neighbour1, neighbour2)

      lookupChildren.get(scope, childrenToChooseFrom)

      verify(repository, times(1)).contains(key1 = scope, key2 = neighbour0)
      verify(repository, times(1)).contains(key1 = scope, key2 = neighbour1)
      verify(repository, times(1)).contains(key1 = scope, key2 = neighbour2)
      verifyNoMoreInteractions(repository)
    }

    "call factoryLookup.convert(id) once for each neighbour" in {
      val (lookupChildren, scope, factoryLookup, _) = build
      val neighbour0 = PozInt(0)
      val neighbour1 = PozInt(1)
      val neighbour2 = PozInt(2)
      val childrenToChooseFrom: Set[PozInt] = Set(neighbour0, neighbour1, neighbour2)

      lookupChildren.get(scope, childrenToChooseFrom)

      verify(factoryLookup, times(1)).convert(id = neighbour0)
      verify(factoryLookup, times(1)).convert(id = neighbour1)
      verify(factoryLookup, times(1)).convert(id = neighbour2)
      verifyNoMoreInteractions(factoryLookup)
    }
  }

  "get (scope, parent)" must {
    "call factoryLookup.convert(id) once with the id of the parent" in {
      val (lookupChildren, scope, factoryLookup, _) = build
      val parent = hasChildrenThatTerminateId

      lookupChildren.get(scope, parent)

      verify(factoryLookup, times(1)).convert(id = parent)
    }

    "call repository.contains once for each neighbour" in {
      val (lookupChildren, scope, _, repository) = build
      val parent = hasChildrenThatTerminateId

      lookupChildren.get(scope, parent)

      verify(repository, times(1)).contains(key1 = scope, key2 = leaf1Id)
      verify(repository, times(1)).contains(key1 = scope, key2 = leaf2Id)
      verifyNoMoreInteractions(repository)
    }
  }

  private def build = {
    val scope = mock[IScope]
    val factoryLookup = new StubFactoryLookupBinding
    val repositoryBinding = new StubRepositoryBinding
    val injector = testInjector(
      factoryLookup, // Override an implementation returned by IoC with a stubbed version.
      repositoryBinding,
      new LookupChildrenBinding
    )
    (injector.getInstance(classOf[LookupChildrenImpl]), scope, factoryLookup.stub, repositoryBinding.stub)
  }
}