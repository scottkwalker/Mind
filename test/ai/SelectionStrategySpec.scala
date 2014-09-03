package ai

import ai.legalGamer.LegalGamer
import composition.TestComposition
import models.common.IScope
import org.mockito.Mockito._

final class SelectionStrategySpec extends TestComposition {

  "chooseChild" must {
    "throw when seq is empty" in {
      val scope = mock[IScope]
      a[RuntimeException] must be thrownBy selectionStrategy.chooseChild(Seq.empty, scope)
    }
  }

  private val selectionStrategy = {
    val rng = mock[RandomNumberGenerator]
    when(rng.nextBoolean).thenReturn(true)
    new LegalGamer(rng)
  }
}