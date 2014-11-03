package ai

import ai.legalGamer.LegalGamer
import composition.{StubRng, TestComposition}
import models.common.IScope
import org.mockito.Mockito._
import scala.concurrent.Future

final class SelectionStrategySpec extends TestComposition {

  "chooseChild" must {
    "throw when seq is empty" in {
      val scope = mock[IScope]
      a[RuntimeException] must be thrownBy selectionStrategy.chooseChild(Future.successful(Seq.empty), scope).futureValue
    }
  }

  private val selectionStrategy = {
    val rng = mock[RandomNumberGenerator]
    when(rng.nextBoolean).thenReturn(true)
    testInjector(new StubRng(rng)).getInstance(classOf[LegalGamer])
  }
}