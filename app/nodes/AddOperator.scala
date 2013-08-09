package nodes

case class AddOperator(val left: Node, val right: Node) extends Node {
  def canTerminate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else left.canTerminate(stepsRemaining - 1) && right.canTerminate(stepsRemaining - 1)
  def toRawScala: String = s"${left.toRawScala} + ${right.toRawScala}"

  def validate(stepsRemaining: Integer): Boolean = {
    if (stepsRemaining == 0) false
    else validate(left, stepsRemaining) && validate(right, stepsRemaining)
  }

  private def validate(n: Node, stepsRemaining: Integer) = {
    n match {
      case _: Value => left.validate(stepsRemaining)
      case _: Empty => false
      case _ => false
    }
  }
}