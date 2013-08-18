package nodes

import nodes.helpers._
import com.google.inject.Guice
import com.tzavellas.sse.guice.ScalaModule
import com.google.inject.Injector

case class AddOperator(val left: Node, val right: Node) extends Node {
  def toRawScala: String = s"${left.toRawScala} + ${right.toRawScala}"

  def validate(stepsRemaining: Integer): Boolean = {
    if (stepsRemaining == 0) false
    else validate(left, stepsRemaining) && validate(right, stepsRemaining)
  }

  private def validate(n: Node, stepsRemaining: Integer) = {
    n match {
      case _: ValueRef => left.validate(stepsRemaining - 1)
      case _: Empty => false
      case _ => false
    }
  }
}

case class AddOperatorFactory() extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(ValueRefFactory())

  def create(scope: Scope): Node = {
    val left = allPossibleChildren(0).create(scope)
    val right = allPossibleChildren(0).create(scope)
    AddOperator(left = left, right = right)
  }
}