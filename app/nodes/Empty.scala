package nodes

case class Empty() extends Node {
  def isTerminal: Boolean = throw new scala.RuntimeException
  def canTerminate(stepsRemaining: Integer): Boolean = throw new scala.RuntimeException
  def toRawScala: String = throw new scala.RuntimeException
  def validate = false
}