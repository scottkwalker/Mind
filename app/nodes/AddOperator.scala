package nodes

case class AddOperator() extends Node {
  def isTerminal: Boolean = false
  def canTerminate(stepsRemaining: Integer): Boolean = ???
  def toRawScala: String = s"${left} + ${right}"
  val left: String = "a"
  val right: String = "b"
}