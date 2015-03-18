package ai

import composition.StubRngBinding
import composition.TestComposition
import composition.UnitTestHelpers
import decision.Decision
import org.mockito.Mockito._

import scala.concurrent.Future

final class SelectionStrategySpec extends UnitTestHelpers with TestComposition {

  "chooseChild" must {
    "throw if the list of possible children is empty" in {
      val (sut, _) = selectionStrategy(nextInt = 42)
      a[RuntimeException] must be thrownBy sut.chooseChild(possibleChildren = Future.successful(Set.empty[Decision])).futureValue
    }

    "return an instance of the expected type" in {
      val (sut, _) = selectionStrategy(nextInt = 0)
      val possibleChildren = Future.successful(Set(mock[Decision]))
      whenReady(sut.chooseChild(possibleChildren)) {
        _ mustBe a[Decision]
      }
    }
  }

  "canAddAnother" must {
    "return true if the accumulator length is zero" in {
      val (sut, _) = selectionStrategy(nextInt = 0)
      sut.canAddAnother(accLength = 0, factoryLimit = 0) must equal(true)
    }

    "return false if the accumulator length is greater than zero and equal to the factory limit" in {
      val (sut, _) = selectionStrategy(nextInt = 1)
      sut.canAddAnother(accLength = 1, factoryLimit = 1) must equal(false)
    }

    "return false if the accumulator length is greater than zero and greater than factory limit" in {
      val (sut, _) = selectionStrategy(nextInt = 1)
      sut.canAddAnother(accLength = 2, factoryLimit = 1) must equal(false)
    }

    "return false if the accumulator length is greater than zero and less than factory limit and rng stubbed to false" in {
      val (sut, _) = selectionStrategy(nextInt = 1, nextBoolean = false)
      sut.canAddAnother(accLength = 1, factoryLimit = 2) must equal(false)
    }

    "return true if the accumulator length is greater than zero and accumulator length less than factory limit and rng stubbed to true" in {
      val (sut, _) = selectionStrategy(nextInt = 1, nextBoolean = true)
      sut.canAddAnother(accLength = 1, factoryLimit = 2) must equal(true)
    }
  }

  "generateLengthOfSeq" must {
    "return a lower bound of 1 even if the random number generator returns zero" in {
      val (sut, _) = selectionStrategy(nextInt = 0)
      sut.generateLengthOfSeq(factoryLimit = 42) must equal(1)
    }

    "call the random number generator's nextInt once with the factoryLimit" in {
      val (sut, randomNumberGenerator) = selectionStrategy(nextInt = 5)
      val factoryLimit = 42

      sut.generateLengthOfSeq(factoryLimit = factoryLimit)

      verify(randomNumberGenerator, times(1)).nextInt(factoryLimit)
      verifyNoMoreInteractions(randomNumberGenerator)
    }

    "returns the random number generator's stubbed value" in {
      val nextInt = 5
      val (sut, _) = selectionStrategy(nextInt = nextInt)
      val factoryLimit = 42

      sut.generateLengthOfSeq(factoryLimit = factoryLimit) must equal(nextInt)
    }

    "throws an exception if the factoryLimit is zero" in {
      val (sut, _) = selectionStrategy(nextInt = 0)
      a[RuntimeException] must be thrownBy sut.generateLengthOfSeq(factoryLimit = 0)
    }
  }

  private def selectionStrategy(nextBoolean: Boolean = true, nextInt: Int) = {
    val randomNumberGenerator = new StubRngBinding(nextBoolean = nextBoolean, nextInt = nextInt)

    val fakeSelectionStrategy = new SelectionStrategy {

      override def chooseIndex(seqLength: Int): Int = ???

      override def chooseChild(possibleChildren: Set[Decision]): Decision = possibleChildren.head

      override protected val rng: RandomNumberGenerator = randomNumberGenerator.stub
    }

    (fakeSelectionStrategy, randomNumberGenerator.stub)
  }
}