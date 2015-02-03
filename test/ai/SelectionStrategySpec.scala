package ai

import ai.aco.Aco
import composition.{StubRngBinding, TestComposition}
import org.mockito.Matchers.any
import org.mockito.Mockito.{when, _}
import replaceEmpty.ReplaceEmpty

import scala.concurrent.Future

final class SelectionStrategySpec extends TestComposition {

  "chooseChild" must {
    "throw when seq is empty" in {
      val (sut, _) = selectionStrategy(nextInt = 42)
      a[RuntimeException] must be thrownBy sut.chooseChild(Future.successful(Set.empty[ReplaceEmpty])).futureValue
    }
  }

  "canAddAnother" must {
    "return true when accumulator length is zero" in {
      val (sut, _) = selectionStrategy(nextInt = 0)
      sut.canAddAnother(accLength = 0, factoryLimit = 0) must equal(true)
    }

    "return false when accumulator length greater than zero and equals than factory limit" in {
      val (sut, _) = selectionStrategy(nextInt = 1)
      sut.canAddAnother(accLength = 1, factoryLimit = 1) must equal(false)
    }

    "return false when accumulator length greater than zero and greater than factory limit" in {
      val (sut, _) = selectionStrategy(nextInt = 1)
      sut.canAddAnother(accLength = 2, factoryLimit = 1) must equal(false)
    }

    "return false when accumulator length greater than zero and less than factory limit and rng stubbed to false" in {
      val (sut, _) = selectionStrategy(nextInt = 1, nextBoolean = false)
      sut.canAddAnother(accLength = 1, factoryLimit = 2) must equal(false)
    }

    "return true when accumulator length greater than zero and accumulator length less than factory limit and rng stubbed to true" in {
      val (sut, _) = selectionStrategy(nextInt = 1, nextBoolean = true)
      sut.canAddAnother(accLength = 1, factoryLimit = 2) must equal(true)
    }
  }

  "generateLengthOfSeq" must {
    "return a minimum of 1 when the random number generator stubbed to return zero" in {
      val (sut, _) = selectionStrategy(nextInt = 0)
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
      val (sut, _) = selectionStrategy(nextInt = nextInt)
      val factoryLimit = 42

      sut.generateLengthOfSeq(factoryLimit = factoryLimit) must equal(nextInt)
    }

    "throws when factoryLimit equals zero" in {
      val (sut, _) = selectionStrategy(nextInt = 0)
      a[RuntimeException] must be thrownBy sut.generateLengthOfSeq(factoryLimit = 0)
    }
  }

  private def selectionStrategy(nextBoolean: Boolean = true, nextInt: Int) = {
    val randomNumberGenerator = new StubRngBinding(nextBoolean = nextBoolean, nextInt = nextInt)
    val ioc = testInjector(
      randomNumberGenerator
    )
    (ioc.getInstance(classOf[Aco]), randomNumberGenerator.stub)
  }
}