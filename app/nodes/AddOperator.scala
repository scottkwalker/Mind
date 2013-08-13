package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope

case class AddOperator(val left: Node, val right: Node) extends Node {
  def toRawScala: String = s"${left.toRawScala} + ${right.toRawScala}"

  def validate(stepsRemaining: Integer): Boolean = {
    validate(left, stepsRemaining) && validate(right, stepsRemaining)
  }

  private def validate(n: Node, stepsRemaining: Integer) = {
    if (stepsRemaining == 0) false
    else n match {
      case _: ValueRef => left.validate(stepsRemaining - 1)
      case _: Empty => false
      case _ => false
    }
  }
}

case object AddOperator extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(ValueRef)

  def create(scope: Scope): Node = {
    val left = allPossibleChildren(0).create(scope)
    val right = allPossibleChildren(0).create(scope)
    AddOperator(left = left, right = right)
  }
}