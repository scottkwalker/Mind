package memoization

import composition.StubFactoryLookupAnyBinding.fakeFactoryDoesNotTerminateId
import composition.StubFactoryLookupAnyBinding.numberOfFactories
import composition._
import models.common.IScope
import models.common.Scope
import models.domain.scala.FactoryLookup
import org.mockito.Matchers
import org.mockito.Matchers._
import org.mockito.Mockito._
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global

class GeneratorImplSpec extends UnitTestHelpers with TestComposition {

  "generate (without futures)" must {
    "return expected message" in {
      val (_, generator, _, _) = buildWithoutFutures
      val scope = mock[IScope]

      whenReady(generator.calculateAndUpdate(scope)) {
        _ must equal(numberOfFactories)
      }(config = patienceConfig)
    }

    "call repository.add once for each factory (when scope has no values)" in {
      val scope = mock[IScope]
      verifyRepositoryAddCalled(scope = scope, expected = 1, builder = buildWithoutFutures)
    }

    "call repository.add once for each scope maxFuncsInObject on each registered factory" in {
      val scope = mock[IScope]
      when(scope.maxFuncsInObject).thenReturn(1)
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, factoryLookup) = buildWithoutFutures

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        factoryLookup.factories.foreach { id =>
          verify(repository, times(1)).add(Matchers.eq(Scope(numFuncs = 0, maxFuncsInObject = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(id))
          verify(repository, times(1)).add(Matchers.eq(Scope(numFuncs = 1, maxFuncsInObject = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(id))
        }
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "call repository.add once for each scope maxParamsInFunc on each registered factory" in {
      val scope = mock[IScope]
      when(scope.maxParamsInFunc).thenReturn(1)
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, factoryLookup) = buildWithoutFutures

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        factoryLookup.factories.foreach { id =>
          verify(repository, atLeastOnce).add(Matchers.eq(Scope(numVals = 0, maxParamsInFunc = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(id))
          verify(repository, atLeastOnce).add(Matchers.eq(Scope(numVals = 1, maxParamsInFunc = 1, height = 1, maxHeight = 1): IScope), Matchers.eq(id))
        }
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "call repository.add once for each scope maxObjectsInTree on each registered factory" in {
      val scope = mock[IScope]
      when(scope.maxObjectsInTree).thenReturn(1)
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, factoryLookup) = buildWithoutFutures

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        factoryLookup.factories.foreach { id =>
          verify(repository, atLeastOnce).add(Matchers.eq(Scope(numObjects = 0, maxObjectsInTree = 1, height = 1, maxHeight = 1): IScope), any[PozInt])
          verify(repository, atLeastOnce).add(Matchers.eq(Scope(numObjects = 1, maxObjectsInTree = 1, height = 1, maxHeight = 1): IScope), any[PozInt])
        }
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "call repository.add once for each scope height on each registered factory" in {
      val scope = mock[IScope]
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, factoryLookup) = buildWithoutFutures

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        factoryLookup.factories.foreach { id =>
          verify(repository, atLeastOnce).add(Matchers.eq(Scope(height = 1, maxHeight = 1): IScope), any[PozInt])
        }
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "does not call repository.add if scope has no height remaining" in {
      val scope = mock[IScope]
      when(scope.maxHeight).thenReturn(0)
      val (_, generator, repository, factoryLookup) = buildWithoutFutures

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        factoryLookup.factories.foreach { id =>
          verify(repository, never).add(any[IScope], any[PozInt])
        }
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "does not call repository.add if scope has height remaining but decision is not a leaf and none of the children terminate" in {
      val scope = mock[IScope]
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository, factoryLookup) = buildDoesNotTerminate

      whenReady(generator.calculateAndUpdate(scope)) { _ =>
        factoryLookup.factories.foreach { id =>
          verify(repository, never).add(any[IScope], any[PozInt])
          verify(repository, times(1)).apply(any[IScope], Matchers.eq(fakeFactoryDoesNotTerminateId))
        }
        verify(repository, times(1)).size
        verifyNoMoreInteractions(repository)
      }(config = patienceConfig)
    }

    "call repository.add once if scope has height remaining and decision is a leaf node" in pending
    "call repository.add once if scope has height remaining and decision has at least one of the children will terminate" in pending
  }

  private def buildWithoutFutures = {
    val lookupChildren = new StubLookupChildrenBinding(size = numberOfFactories)
    val repository = new StubRepositoryBinding
    val factoryLookup = new StubFactoryLookupAnyBinding
    val generator = testInjector(
      factoryLookup,
      lookupChildren,
      repository,
      new GeneratorBinding
    ).getInstance(classOf[Generator])
    (lookupChildren.stub, generator, repository.stub, factoryLookup.stub)
  }

  private def buildDoesNotTerminate = {
    val lookupChildren = new StubLookupChildrenBinding(size = numberOfFactories)
    val repository = new StubRepositoryBinding
    val factoryLookup = new StubFactoryDoesNotTerminateBinding
    val generator = testInjector(
      factoryLookup,
      lookupChildren,
      repository,
      new GeneratorBinding
    ).getInstance(classOf[Generator])
    (lookupChildren.stub, generator, repository.stub, factoryLookup.stub)
  }

  private def verifyRepositoryAddCalled(scope: IScope, expected: Int = 2, builder: => (LookupChildren, Generator, Memoize2WithSet[IScope, PozInt], FactoryLookup)) = {
    val (_, generator: Generator, repository: Memoize2WithSet[IScope, PozInt], _) = builder
    generator.calculateAndUpdate(scope).map { _ =>
      verify(repository, times(expected)).add(any[IScope], any[PozInt])
      verifyNoMoreInteractions(repository)
    }
  }
}
