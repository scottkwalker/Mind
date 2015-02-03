package memoization

import composition.StubFactoryLookupBinding.numberOfFactories
import composition._
import models.common.{IScope, Scope}
import models.domain.scala.FactoryLookup
import org.mockito.Matchers
import org.mockito.Matchers._
import org.mockito.Mockito._
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global

class GeneratorImplSpec extends TestComposition {

  "generate (without futures)" must {
    "return expected message" in {
      val (_, generator, _) = buildWithoutFutures
      val scope = mock[IScope]
      whenReady(generator.calculateAndUpdate(scope)) {
        _ must equal(numberOfFactories)
      }(config = patienceConfig)
    }

    "call repository.add once for each factory (when scope has no values)" in {
      val scope = mock[IScope]
      verfifyRepositoryAddCalled(scope = scope, expected = 1, builder = buildWithoutFutures)
    }

    "call repository.add once for each scope maxFuncsInObject" in {
      val scope = mock[IScope]
      when(scope.maxFuncsInObject).thenReturn(1)
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository) = buildWithoutFutures
      whenReady(generator.calculateAndUpdate(scope)) { r =>
        verify(repository, atLeastOnce).add(Matchers.eq(Scope(numFuncs = 0, maxFuncsInObject = 1, height = 1, maxHeight = 1): IScope), any[PozInt])
        verify(repository, atLeastOnce).add(Matchers.eq(Scope(numFuncs = 1, maxFuncsInObject = 1, height = 1, maxHeight = 1): IScope), any[PozInt])
      }(config = patienceConfig)
    }

    "call repository.add once for each scope maxParamsInFunc" in {
      val scope = mock[IScope]
      when(scope.maxParamsInFunc).thenReturn(1)
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository) = buildWithoutFutures
      whenReady(generator.calculateAndUpdate(scope)) { r =>
        verify(repository, atLeastOnce).add(Matchers.eq(Scope(numVals = 0, maxParamsInFunc = 1, height = 1, maxHeight = 1): IScope), any[PozInt])
        verify(repository, atLeastOnce).add(Matchers.eq(Scope(numVals = 1, maxParamsInFunc = 1, height = 1, maxHeight = 1): IScope), any[PozInt])
      }(config = patienceConfig)
    }

    "call repository.add once for each scope maxObjectsInTree" in {
      val scope = mock[IScope]
      when(scope.maxObjectsInTree).thenReturn(1)
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository) = buildWithoutFutures
      whenReady(generator.calculateAndUpdate(scope)) { r =>
        verify(repository, atLeastOnce).add(Matchers.eq(Scope(numObjects = 0, maxObjectsInTree = 1, height = 1, maxHeight = 1): IScope), any[PozInt])
        verify(repository, atLeastOnce).add(Matchers.eq(Scope(numObjects = 1, maxObjectsInTree = 1, height = 1, maxHeight = 1): IScope), any[PozInt])
      }(config = patienceConfig)
    }

    "call repository.add once for each scope height" in {
      val scope = mock[IScope]
      when(scope.maxHeight).thenReturn(1)
      val (_, generator, repository) = buildWithoutFutures
      whenReady(generator.calculateAndUpdate(scope)) { r =>
        verify(repository, atLeastOnce).add(Matchers.eq(Scope(height = 1, maxHeight = 1): IScope), any[PozInt])
      }(config = patienceConfig)
    }
  }

  private def buildWithoutFutures: (LookupChildren, Generator, Memoize2WithSet[IScope, PozInt]) = {
    val lookupChildren: LookupChildren = mock[LookupChildren]
    val repository = mock[Memoize2WithSet[IScope, PozInt]]
    val factoryLookup = new StubFactoryLookupBinding
    val generator = testInjector(
      factoryLookup,
      new StubLookupChildrenBinding(lookupChildren, size = numberOfFactories),
      new StubRepositoryBinding(repository)
    ).getInstance(classOf[Generator])
    (lookupChildren, generator, repository)
  }

  private def verfifyRepositoryAddCalled(scope: IScope, expected: Int = 2, builder: => (LookupChildren, Generator, Memoize2WithSet[IScope, PozInt])) = {
    val (_, generator: Generator, repository: Memoize2WithSet[IScope, PozInt]) = builder
    generator.calculateAndUpdate(scope).map { _ =>
      verify(repository, times(expected)).add(any[IScope], any[PozInt])
    }
  }
}
