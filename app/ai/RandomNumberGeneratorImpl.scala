package ai

import scala.util.Random

final class RandomNumberGeneratorImpl extends RandomNumberGenerator {

  private val rng: Random = new Random

  def nextBoolean: Boolean = rng.nextBoolean()

  def nextInt(n: Int) = rng.nextInt(n)
}
