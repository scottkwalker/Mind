package nodes

case class Empty() extends Node {
  def toRawScala: String = throw new scala.RuntimeException
  def validate(stepsRemaining: Integer): Boolean = false
}