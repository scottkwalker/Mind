package ai

import ai.legalGamer.LegalGamer
import nodes.helpers.IScope
import org.mockito.Mockito._
import utils.helpers.UnitSpec

final class SelectionStrategySpec extends UnitSpec {

  "chooseChild" should {
    "throw when seq is empty" in {
      val scope = mock[IScope]
      a[RuntimeException] should be thrownBy selectionStrategy.chooseChild(Seq.empty, scope)
    }
  }

  private val selectionStrategy = {
    val rng = mock[IRandomNumberGenerator]
    when(rng.nextBoolean).thenReturn(true)
    new LegalGamer(rng)
  }
}
