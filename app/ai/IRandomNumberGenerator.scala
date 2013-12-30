package ai

trait IRandomNumberGenerator {
  def nextBoolean(): Boolean
  def nextInt(n: Int): Int
}
