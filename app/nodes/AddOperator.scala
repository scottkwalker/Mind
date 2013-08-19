package nodes

import nodes.helpers._
import com.google.inject.Guice
import com.tzavellas.sse.guice.ScalaModule
import com.google.inject.Injector
import com.google.inject.Inject

case class AddOperator(val left: Node, val right: Node) extends Node {
  def toRawScala: String = s"${left.toRawScala} + ${right.toRawScala}"

  def validate(scope: Scope): Boolean = {
    if (scope.noStepsRemaining) false
    else validate(left, scope) && validate(right, scope)
  }

  private def validate(n: Node, scope: Scope) = {
    n match {
      case _: ValueRef => n.validate(scope.decrementStepsRemaining)
      case _: Empty => false
      case _ => false
    }
  }
}

case class AddOperatorFactory @Inject() (injector: Injector) extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[ValueRefFactory]))

  def create(scope: Scope): Node = {
    val possibleChildren = validChildren(scope)
    val left = possibleChildren(0).create(scope)
    val right = possibleChildren(0).create(scope)
    AddOperator(left = left, right = right)
  }
}