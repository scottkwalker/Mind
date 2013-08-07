package nodes

case class AddOperator(val left: Node, val right: Node) extends Node {
  def canTerminate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else left.canTerminate(stepsRemaining - 1) && right.canTerminate(stepsRemaining - 1)
  def toRawScala: String = s"${left.toRawScala} + ${right.toRawScala}"
  def hasNoEmptyNodes = left.hasNoEmptyNodes && right.hasNoEmptyNodes

  def validate: Boolean = {
    val l = left match {
      case Value(_) => left.validate
      case _ => false
    }
    lazy val r = right match {
      case Value(_) => left.validate
      case _ => false
    }
    l && r
  }
}