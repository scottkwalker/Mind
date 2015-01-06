package memoization

import composition.{StubLookupChildren, TestComposition}
import models.common.{Scope, IScope}
import models.domain.scala.FactoryLookup
import org.mockito.Matchers
import org.mockito.Matchers._
import org.mockito.Mockito._
import utils.PozInt

class GeneratorImplSpec extends TestComposition {

  "generate" must {
    "return true" in {
      val (_, generator) = build
      val scope = mock[IScope]
      whenReady(generator.generate(scope)) {
        _ must equal(true)
      }
    }

    "call lookupChildren.fetch once for each factory (when scope has no values)" in {
      val scope = mock[IScope]
      verfifyLookupChildrenCalled(scope, expected = 1)
    }

    "call lookupChildren.fetch once for each scope maxFuncsInObject" in {
      val scope = mock[IScope]
      when(scope.maxFuncsInObject).thenReturn(1)
      val (lookupChildren, generator) = build
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numFuncs = 0)), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numFuncs = 1)), any[PozInt])
      }
    }

    "call lookupChildren.fetch once for each scope maxParamsInFunc" in {
      val scope = mock[IScope]
      when(scope.maxParamsInFunc).thenReturn(1)
      val (lookupChildren, generator) = build
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numVals = 0)), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numVals = 1)), any[PozInt])
      }
    }

    "call lookupChildren.fetch once for each scope maxObjectsInTree" in {
      val scope = mock[IScope]
      when(scope.maxObjectsInTree).thenReturn(1)
      val (lookupChildren, generator) = build
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numObjects = 0)), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(numObjects = 1)), any[PozInt])
      }
    }

    "call lookupChildren.fetch once for each scope height" in {
      val scope = mock[IScope]
      when(scope.height).thenReturn(1)
      val (lookupChildren, generator) = build
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(height = 0)), any[PozInt])
        verify(lookupChildren, times(1)).fetch(Matchers.eq(Scope(height = 1)), any[PozInt])
      }
    }
  }

  private def build = {
    val factoryLookup: FactoryLookup = mock[FactoryLookup]
    when(factoryLookup.factories).thenReturn(Set(new PozInt(0)))
    val lookupChildren: LookupChildren = mock[LookupChildren]
    when(lookupChildren.factoryLookup).thenReturn(factoryLookup)
    val generator = testInjector(new StubLookupChildren(lookupChildren)).getInstance(classOf[Generator])
    (lookupChildren, generator)
  }

  private def verfifyLookupChildrenCalled(scope: IScope, expected: Int = 2) = {
    val (lookupChildren, generator) = build
    whenReady(generator.generate(scope)) { r =>
      verify(lookupChildren, times(expected)).fetch(any[IScope], any[PozInt])
    }
  }
}
