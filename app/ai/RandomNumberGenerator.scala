package ai

import scala.util.Random

case class RandomNumberGenerator() extends IRandomNumberGenerator {
  val rng: Random = new Random
  def nextBoolean() = rng.nextBoolean()
  def nextInt(n: Int) = rng.nextInt(n)
}
