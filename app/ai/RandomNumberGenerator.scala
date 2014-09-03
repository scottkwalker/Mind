package ai

trait RandomNumberGenerator {

  def nextBoolean: Boolean

  def nextInt(n: Int): Int
}
