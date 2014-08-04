package ai

import utils.helpers.UnitSpec

final class RandomNumberGeneratorSpec extends UnitSpec {

  "nextBoolean" should {
    "return a boolean when called" in {
      rng.nextBoolean shouldBe a[java.lang.Boolean]
    }
  }

  private val rng = new RandomNumberGenerator()
}
