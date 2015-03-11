package ai

import composition.RandomNumberGeneratorBinding
import composition.TestComposition
import composition.UnitTestHelpers

final class RandomNumberGeneratorImplSpec extends UnitTestHelpers with TestComposition {

  "nextBoolean" must {
    "return a boolean" in {
      randomNumberGenerator.nextBoolean mustBe a[java.lang.Boolean]
    }
  }

  "nextInt" must {
    "return an integer" in {
      randomNumberGenerator.nextInt(1) mustBe a[java.lang.Integer]
    }
  }

  private def randomNumberGenerator = testInjector(new RandomNumberGeneratorBinding).getInstance(classOf[RandomNumberGenerator])
}