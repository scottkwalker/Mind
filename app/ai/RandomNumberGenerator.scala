package ai

import scala.util.Random

final case class RandomNumberGenerator() extends IRandomNumberGenerator {

  val rng: Random = new Random

  def nextBoolean() = rng.nextBoolean()

  def nextInt(n: Int) = rng.nextInt(n)
}
