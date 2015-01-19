package memoization

import composition.{StubLookupChildren, TestComposition}
import models.common.{Scope, IScope}
import models.domain.scala.FactoryLookup
import org.mockito.Matchers
import org.mockito.Matchers._
import org.mockito.Mockito._
import utils.PozInt
import scala.concurrent.ExecutionContext.Implicits.global

class GeneratorImplSpec extends TestComposition {

  "generate (with futures)" must {
    "return true" in {
      val (_, generator) = build
      val scope = mock[IScope]
      whenReady(generator.generate(scope)) {
        _ must equal(true)
      }
    }

    "call lookupChildren.fetch once for each factory (when scope has no values)" in {
      val scope = mock[IScope]
      verfifyLookupChildrenCalled(scope = scope, expected = 1, builder = build)
    }

    "call lookupChildren.fetch once for each scope maxFuncsInObject" in {
      val scope = mock[IScope]
      when(scope.maxFuncsInObject).thenReturn(1)
      val (lookupChildren, generator) = build
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numFuncs = 0, maxFuncsInObject = 1): IScope), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numFuncs = 1, maxFuncsInObject = 1): IScope), any[PozInt])
      }
    }

    "call lookupChildren.fetch once for each scope maxParamsInFunc" in {
      val scope = mock[IScope]
      when(scope.maxParamsInFunc).thenReturn(1)
      val (lookupChildren, generator) = build
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numVals = 0, maxParamsInFunc = 1): IScope), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numVals = 1, maxParamsInFunc = 1): IScope), any[PozInt])
      }
    }

    "call lookupChildren.fetch once for each scope maxObjectsInTree" in {
      val scope = mock[IScope]
      when(scope.maxObjectsInTree).thenReturn(1)
      val (lookupChildren, generator) = build
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numObjects = 0, maxObjectsInTree = 1): IScope), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numObjects = 1, maxObjectsInTree = 1): IScope), any[PozInt])
      }
    }

    "call lookupChildren.fetch once for each scope height" in {
      val scope = mock[IScope]
      when(scope.maxHeight).thenReturn(1)
      val (lookupChildren, generator) = build
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(height = 0, maxHeight = 1): IScope), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(height = 1, maxHeight = 1): IScope), any[PozInt])
      }
    }
  }

  "generate (without futures)" must {
    "return true" in {
      val (_, generator) = buildWithoutFutures
      val scope = mock[IScope]
      whenReady(generator.generate(scope)) {
        _ must equal(true)
      }
    }

    "call lookupChildren.fetch once for each factory (when scope has no values)" in {
      val scope = mock[IScope]
      verfifyLookupChildrenCalled(scope = scope, expected = 1, builder = buildWithoutFutures)
    }

    "call lookupChildren.fetch once for each scope maxFuncsInObject" in {
      val scope = mock[IScope]
      when(scope.maxFuncsInObject).thenReturn(1)
      val (lookupChildren, generator) = buildWithoutFutures
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numFuncs = 0, maxFuncsInObject = 1): IScope), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numFuncs = 1, maxFuncsInObject = 1): IScope), any[PozInt])
      }
    }

    "call lookupChildren.fetch once for each scope maxParamsInFunc" in {
      val scope = mock[IScope]
      when(scope.maxParamsInFunc).thenReturn(1)
      val (lookupChildren, generator) = buildWithoutFutures
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numVals = 0, maxParamsInFunc = 1): IScope), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numVals = 1, maxParamsInFunc = 1): IScope), any[PozInt])
      }
    }

    "call lookupChildren.fetch once for each scope maxObjectsInTree" in {
      val scope = mock[IScope]
      when(scope.maxObjectsInTree).thenReturn(1)
      val (lookupChildren, generator) = buildWithoutFutures
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numObjects = 0, maxObjectsInTree = 1): IScope), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numObjects = 1, maxObjectsInTree = 1): IScope), any[PozInt])
      }
    }

    "call lookupChildren.fetch once for each scope height" in {
      val scope = mock[IScope]
      when(scope.maxHeight).thenReturn(1)
      val (lookupChildren, generator) = buildWithoutFutures
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(height = 0, maxHeight = 1): IScope), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(height = 1, maxHeight = 1): IScope), any[PozInt])
      }
    }
  }

  private def build: (LookupChildren, Generator) = {
    val lookupChildren = buildLookupChildren
    val generator = testInjector(new StubLookupChildren(lookupChildren)).getInstance(classOf[Generator])
    (lookupChildren, generator)
  }

  private def buildWithoutFutures: (LookupChildren, Generator) = {
    val lookupChildren = buildLookupChildren
    val generator = testInjector(new StubLookupChildren(lookupChildren)).getInstance(classOf[Generator])
    (lookupChildren, generator)
  }

  private def buildLookupChildren: LookupChildren = {
    val factoryLookup: FactoryLookup = mock[FactoryLookup]
    when(factoryLookup.factories).thenReturn(Set(new PozInt(0)))
    val lookupChildren: LookupChildren = mock[LookupChildren]
    when(lookupChildren.factoryLookup).thenReturn(factoryLookup)
    lookupChildren
  }

  private def verfifyLookupChildrenCalled(scope: IScope, expected: Int = 2, builder: => (LookupChildren, Generator)) = {
    val (lookupChildren: LookupChildren, generator: Generator) = builder
    generator.generate(scope).map { _ =>
      verify(lookupChildren, times(expected)).fetch(any[IScope], any[PozInt])
    }
  }
}
