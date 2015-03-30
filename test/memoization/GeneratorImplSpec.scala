package memoization

import com.google.inject.AbstractModule
import composition.StubFactoryLookupAnyBinding.doesNotTerminateId
import composition.StubFactoryLookupAnyBinding.hasChildrenThatTerminateId
import composition.StubFactoryLookupAnyBinding.leaf2Id
import composition.StubFactoryLookupAnyBinding.numberOfFactories
import composition.StubFactoryLookupAnyBinding.leaf1Id
import composition._
import models.common.{IScope, Scope}
import models.domain.scala.FactoryLookup
import org.mockito.Matchers
import org.mockito.Matchers._
import org.mockito.Mockito._
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global

class GeneratorImplSpec extends UnitTestHelpers with TestComposition {

  "generate (without futures)" must {
    "return expected message" in {
      val (_, generator, _, _) = buildMixedDecisions
      val scope = mock[IScope]

      whenReady(generator.calculateAndUpdate(scope)) {
        _ must equal(numberOfFactories)
      }(config = patienceConfig)
    }

    "does not call repository.add if scope has no height remaining" in {
      val scope = mock[IScope]
      when(scope.maxHeight).thenReturn(0)
      val (_, generator, repository, _) = buildMixedDecisions

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "does not call repository.add if scope has height remaining but decision is not a leaf and none of the children terminate" in {
      val scope = mock[IScope]
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, _) = buildWithDecisionThatDoesNotTerminate

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        verify(repository, times(1)).contains(any[IScope], Matchers.eq(doesNotTerminateId))
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "call repository.add once if scope has height remaining and decision is a leaf node" in {
      val scope = mock[IScope]
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, _) = buildWithDecisionThatIsLeaf

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        verify(repository, times(1)).add(any[IScope], Matchers.eq(leaf1Id))
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "call repository.add once if scope has height remaining and decision has at least one of the children will terminate" in {
      val scope = mock[IScope]
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, _) = buildWithDecisionThatHasChildrenThatTerminate

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        verify(repository, times(1)).contains(any[IScope], Matchers.eq(leaf1Id))
        verify(repository, times(1)).add(any[IScope], Matchers.eq(hasChildrenThatTerminateId))
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "call repository.add once for each factory (when scope has no values)" in {
      val scope = mock[IScope]
      verifyRepositoryAddCalled(scope = scope, expected = 1, builder = buildMixedDecisions)
    }

    "call repository.add once for each scope maxFuncsInObject on each registered factory" in {
      val scope = mock[IScope]
      when(scope.maxFuncsInObject).thenReturn(1)
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, factoryLookup) = buildMixedDecisions

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        factoryLookup.factories.foreach {
          case `doesNotTerminateId` =>
            verify(repository, times(1)).contains(Matchers.eq(Scope(numFuncs = 0, maxFuncsInObject = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(doesNotTerminateId))
            verify(repository, times(1)).contains(Matchers.eq(Scope(numFuncs = 1, maxFuncsInObject = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(doesNotTerminateId))
          case `leaf1Id` =>
            verify(repository, times(1)).add(Matchers.eq(Scope(numFuncs = 0, maxFuncsInObject = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
            verify(repository, times(1)).add(Matchers.eq(Scope(numFuncs = 1, maxFuncsInObject = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
          case `leaf2Id` =>
            verify(repository, times(1)).add(Matchers.eq(Scope(numFuncs = 0, maxFuncsInObject = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf2Id))
            verify(repository, times(1)).add(Matchers.eq(Scope(numFuncs = 1, maxFuncsInObject = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf2Id))
          case `hasChildrenThatTerminateId` =>
            verify(repository, times(1)).contains(Matchers.eq(Scope(numFuncs = 0, maxFuncsInObject = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
            verify(repository, times(1)).contains(Matchers.eq(Scope(numFuncs = 1, maxFuncsInObject = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
            verify(repository, times(1)).add(Matchers.eq(Scope(numFuncs = 0, maxFuncsInObject = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(hasChildrenThatTerminateId))
            verify(repository, times(1)).add(Matchers.eq(Scope(numFuncs = 1, maxFuncsInObject = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(hasChildrenThatTerminateId))
        }
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "call repository.add once for each scope maxParamsInFunc on each registered factory" in {
      val scope = mock[IScope]
      when(scope.maxParamsInFunc).thenReturn(1)
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, factoryLookup) = buildMixedDecisions

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        factoryLookup.factories.foreach {
          case `doesNotTerminateId` =>
            verify(repository, times(1)).contains(Matchers.eq(Scope(numVals = 0, maxParamsInFunc = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(doesNotTerminateId))
            verify(repository, times(1)).contains(Matchers.eq(Scope(numVals = 1, maxParamsInFunc = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(doesNotTerminateId))
          case `leaf1Id` =>
            verify(repository, times(1)).add(Matchers.eq(Scope(numVals = 0, maxParamsInFunc = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
            verify(repository, times(1)).add(Matchers.eq(Scope(numVals = 1, maxParamsInFunc = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
          case `leaf2Id` =>
            verify(repository, times(1)).add(Matchers.eq(Scope(numVals = 0, maxParamsInFunc = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf2Id))
            verify(repository, times(1)).add(Matchers.eq(Scope(numVals = 1, maxParamsInFunc = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf2Id))
          case `hasChildrenThatTerminateId` =>
            verify(repository, times(1)).contains(Matchers.eq(Scope(numVals = 0, maxParamsInFunc = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
            verify(repository, times(1)).contains(Matchers.eq(Scope(numVals = 1, maxParamsInFunc = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
            verify(repository, times(1)).add(Matchers.eq(Scope(numVals = 0, maxParamsInFunc = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(hasChildrenThatTerminateId))
            verify(repository, times(1)).add(Matchers.eq(Scope(numVals = 1, maxParamsInFunc = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(hasChildrenThatTerminateId))
        }
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "call repository.add once for each scope maxObjectsInTree on each registered factory" in {
      val scope = mock[IScope]
      when(scope.maxObjectsInTree).thenReturn(1)
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, factoryLookup) = buildMixedDecisions

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        factoryLookup.factories.foreach {
          case `doesNotTerminateId` =>
            verify(repository, times(1)).contains(Matchers.eq(Scope(numObjects = 0, maxObjectsInTree = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(doesNotTerminateId))
            verify(repository, times(1)).contains(Matchers.eq(Scope(numObjects = 1, maxObjectsInTree = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(doesNotTerminateId))
          case `leaf1Id` =>
            verify(repository, times(1)).add(Matchers.eq(Scope(numObjects = 0, maxObjectsInTree = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
            verify(repository, times(1)).add(Matchers.eq(Scope(numObjects = 1, maxObjectsInTree = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
          case `leaf2Id` =>
            verify(repository, times(1)).add(Matchers.eq(Scope(numObjects = 0, maxObjectsInTree = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf2Id))
            verify(repository, times(1)).add(Matchers.eq(Scope(numObjects = 1, maxObjectsInTree = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(leaf2Id))
          case `hasChildrenThatTerminateId` =>
            verify(repository, times(1)).contains(Matchers.eq(Scope(numObjects = 0, maxObjectsInTree = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
            verify(repository, times(1)).contains(Matchers.eq(Scope(numObjects = 1, maxObjectsInTree = 1, height = 0, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
            verify(repository, times(1)).add(Matchers.eq(Scope(numObjects = 0, maxObjectsInTree = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(hasChildrenThatTerminateId))
            verify(repository, times(1)).add(Matchers.eq(Scope(numObjects = 1, maxObjectsInTree = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(hasChildrenThatTerminateId))
        }
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "call repository.add once for each scope height on each registered factory" in {
      val scope = mock[IScope]
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, factoryLookup) = buildMixedDecisions

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        factoryLookup.factories.foreach {
          case `doesNotTerminateId` =>
            verify(repository, times(1)).contains(Matchers.eq(Scope(height = 0, maxHeight = 1): IScope), Matchers.eq(doesNotTerminateId))
          case `leaf1Id` =>
            verify(repository, times(1)).add(Matchers.eq(Scope(height = 1, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
          case `leaf2Id` =>
            verify(repository, times(1)).add(Matchers.eq(Scope(height = 1, maxHeight = 1): IScope), Matchers.eq(leaf2Id))
          case `hasChildrenThatTerminateId` =>
            verify(repository, times(1)).contains(Matchers.eq(Scope(height = 0, maxHeight = 1): IScope), Matchers.eq(leaf1Id))
            verify(repository, times(1)).add(Matchers.eq(Scope(height = 1, maxHeight = 1): IScope), Matchers.eq(hasChildrenThatTerminateId))
        }
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }
  }

  private def buildMixedDecisions = {
    val factoryLookup = new StubFactoryLookupAnyBinding
    val (lookupChildren, generator, repository) = build(factoryLookup)
    (lookupChildren, generator, repository, factoryLookup.stub)
  }

  private def buildWithDecisionThatDoesNotTerminate = {
    val factoryLookup = new StubFactoryWithDecisionThatDoesNotTerminateBinding
    val (lookupChildren, generator, repository) = build(factoryLookup)
    (lookupChildren, generator, repository, factoryLookup.stub)
  }

  private def buildWithDecisionThatIsLeaf = {
    val factoryLookup = new StubFactoryWithDecisionThatIsLeafBinding
    val (lookupChildren, generator, repository) = build(factoryLookup)
    (lookupChildren, generator, repository, factoryLookup.stub)
  }

  private def buildWithDecisionThatHasChildrenThatTerminate = {
    val factoryLookup = new StubFactoryWithDecisionThatHasChildrenThatTerminateBinding
    val (lookupChildren, generator, repository) = build(factoryLookup)
    (lookupChildren, generator, repository, factoryLookup.stub)
  }

  private def build(factoryLookup: AbstractModule) = {
    val lookupChildren = new StubLookupChildrenBinding(size = numberOfFactories)
    val repository = new StubRepositoryBinding
    val generator = testInjector(
      factoryLookup,
      lookupChildren,
      repository,
      new GeneratorBinding
    ).getInstance(classOf[Generator])
    (lookupChildren.stub, generator, repository.stub)
  }

  private def verifyRepositoryAddCalled(scope: IScope, expected: Int = 2, builder: => (LookupChildren, Generator, Memoize2WithSet[IScope, PozInt], FactoryLookup)) = {
    val (_, generator: Generator, repository: Memoize2WithSet[IScope, PozInt], _) = builder
    generator.calculateAndUpdate(scope).map { _ =>
      verify(repository, times(expected)).add(any[IScope], any[PozInt])
      verifyNoMoreInteractions(repository)
    }
  }
}
