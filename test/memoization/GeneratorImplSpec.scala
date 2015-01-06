package memoization

import composition.{StubLookupChildren, TestComposition}
import models.common.IScope
import models.domain.scala.FactoryLookup
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

    "call lookupChildren.fetch once for each scope maxExpressionsInFunc" in {
      val scope = mock[IScope]
      when(scope.maxExpressionsInFunc).thenReturn(1)
      verfifyLookupChildrenCalled(scope)
    }

    "call lookupChildren.fetch once for each scope maxFuncsInObject" in {
      val scope = mock[IScope]
      when(scope.maxFuncsInObject).thenReturn(1)
      verfifyLookupChildrenCalled(scope)
    }

    "call lookupChildren.fetch once for each scope maxParamsInFunc" in {
      val scope = mock[IScope]
      when(scope.maxParamsInFunc).thenReturn(1)
      verfifyLookupChildrenCalled(scope)
    }

    "call lookupChildren.fetch once for each scope maxObjectsInTree" in {
      val scope = mock[IScope]
      when(scope.maxObjectsInTree).thenReturn(1)
      verfifyLookupChildrenCalled(scope)
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
