package ai

import composition.TestComposition

final class RandomNumberGeneratorImplSpec extends TestComposition {

  "nextBoolean" must {
    "return a boolean when called" in {
      rng.nextBoolean mustBe a[java.lang.Boolean]
    }
  }

  private val rng = testInjector().getInstance(classOf[RandomNumberGenerator])
}