package ai

import composition.TestComposition

final class RandomNumberGeneratorImplSpec extends TestComposition {

  "nextBoolean" must {
    "return a boolean when called" in {
      randomNumberGenerator.nextBoolean mustBe a[java.lang.Boolean]
    }
  }

  private def randomNumberGenerator = testInjector().getInstance(classOf[RandomNumberGenerator])
}