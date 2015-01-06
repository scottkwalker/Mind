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

    "call lookupChildren.fetch once for each factory (when scope allows only one call)" in {
      val (lookupChildren, generator) = build
      val scope = mock[IScope]
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(3)).fetch(any[IScope], any[PozInt])
      }
    }
  }

  private def build = {
    val factoryLookup: FactoryLookup = mock[FactoryLookup]
    when(factoryLookup.factories).thenReturn(Set(new PozInt(0), new PozInt(1), new PozInt(2)))
    val lookupChildren: LookupChildren = mock[LookupChildren]
    when(lookupChildren.factoryLookup).thenReturn(factoryLookup)
    val generator = testInjector(new StubLookupChildren(lookupChildren)).getInstance(classOf[Generator])
    (lookupChildren, generator)
  }
}
