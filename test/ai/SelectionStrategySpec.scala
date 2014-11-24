package ai

import ai.legalGamer.LegalGamer
import composition.{StubRng, TestComposition}
import models.common.IScope
import org.mockito.Mockito._
import scala.concurrent.Future
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}

final class SelectionStrategySpec extends TestComposition {

  "chooseChild" must {
    "throw when seq is empty" in {
      val scope = mock[IScope]
      val (sut, rng) = selectionStrategy(nextInt = 42)
      a[RuntimeException] must be thrownBy sut.chooseChild(Future.successful(Seq.empty), scope).futureValue
    }
  }

  "generateLengthOfSeq" must {
    "return a minimum of 1 when the random number generator stubbed to return zero" in {
      val (sut, rng) = selectionStrategy(nextInt = 0)
      sut.generateLengthOfSeq(factoryLimit = 42) must equal(1)
    }

    "calls random number generator with the limit passed in" in {
      val (sut, rng) = selectionStrategy(nextInt = 5)
      val factoryLimit = 42

      sut.generateLengthOfSeq(factoryLimit = factoryLimit)

      verify(rng, times(1)).nextInt(factoryLimit)
    }

    "returns random number generator stubbed value when greater than zero" in {
      val nextInt = 5
      val (sut, rng) = selectionStrategy(nextInt = nextInt)
      val factoryLimit = 42

      sut.generateLengthOfSeq(factoryLimit = factoryLimit) must equal(nextInt)
    }
  }

  private def selectionStrategy(nextBoolean: Boolean = true, nextInt: Int) = {
    val rng = mock[RandomNumberGenerator]
    when(rng.nextBoolean).thenReturn(nextBoolean)
    when(rng.nextInt(any[Int])).thenReturn(nextInt)
    val ioc = testInjector(new StubRng(rng))
    (ioc.getInstance(classOf[LegalGamer]), ioc.getInstance(classOf[RandomNumberGenerator]) )
  }
}