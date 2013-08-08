package nodes

case class AddOperator(val left: Node, val right: Node) extends Node {
  def canTerminate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else left.canTerminate(stepsRemaining - 1) && right.canTerminate(stepsRemaining - 1)
  def toRawScala: String = s"${left.toRawScala} + ${right.toRawScala}"

  def validate: Boolean = {
    validate(left) && validate(right)
  }
  
  private def validate(n: Node) = {
    n match {
      case Value(_) => left.validate
      case Empty() => false
      case _ => false
    }
  }
}