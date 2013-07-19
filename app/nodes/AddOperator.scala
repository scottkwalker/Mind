package nodes

case class AddOperator(val left: Node, val right: Node) extends Node {
  def isTerminal: Boolean = false
  def canTerminate(stepsRemaining: Integer): Boolean = left.canTerminate(stepsRemaining - 1) && right.canTerminate(stepsRemaining - 1)
  def toRawScala: String = s"${left.toRawScala} + ${right.toRawScala}"
  def validate = left.validate && right.validate
}