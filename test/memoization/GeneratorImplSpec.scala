package memoization

import composition.{StubLookupChildren, TestComposition}
import models.common.IScope
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

    "call lookupChildren.fetch" in {
      val (lookupChildren, generator) = build
      val scope = mock[IScope]
      whenReady(generator.generate(scope)) { r =>
        verify(lookupChildren, times(1)).fetch(any[IScope], any[PozInt])
      }
    }
  }

  private def build = {
    val lookupChildren: LookupChildren = mock[LookupChildren]
    val generator = testInjector(new StubLookupChildren(lookupChildren)).getInstance(classOf[Generator])
    (lookupChildren, generator)
  }
}
