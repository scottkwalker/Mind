package ai

import composition.TestComposition
import composition.UnitTestHelpers

final class RandomNumberGeneratorImplSpec extends UnitTestHelpers with TestComposition {

  "nextBoolean" must {
    "return a boolean when called" in {
      randomNumberGenerator.nextBoolean mustBe a[java.lang.Boolean]
    }
  }

  "nextInt" must {
    "return an int when called" in {
      randomNumberGenerator.nextInt(1) mustBe a[java.lang.Integer]
    }
  }

  private def randomNumberGenerator = testInjector().getInstance(classOf[RandomNumberGenerator])
}