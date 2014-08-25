package ai

import composition.TestComposition

final class RandomNumberGeneratorSpec extends TestComposition {

  "nextBoolean" must {
    "return a boolean when called" in {
      rng.nextBoolean mustBe a[java.lang.Boolean]
    }
  }

  private val rng = new RandomNumberGenerator()
}