package ai

import ai.legalGamer.LegalGamer
import models.common.IScope
import org.mockito.Mockito._
import composition.TestComposition

final class SelectionStrategySpec extends TestComposition {

  "chooseChild" must {
    "throw when seq is empty" in {
      val scope = mock[IScope]
      a[RuntimeException] must be thrownBy selectionStrategy.chooseChild(Seq.empty, scope)
    }
  }

  private val selectionStrategy = {
    val rng = mock[IRandomNumberGenerator]
    when(rng.nextBoolean).thenReturn(true)
    new LegalGamer(rng)
  }
}
