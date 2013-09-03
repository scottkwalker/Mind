package nodes

import nodes.helpers._
import com.google.inject.Guice
import com.tzavellas.sse.guice.ScalaModule
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai
import scala.annotation.tailrec

case class AddOperator(val left: Node, val right: Node) extends Node {
  override def toRawScala: String = s"${left.toRawScala} + ${right.toRawScala}"

  override def validate(scope: Scope): Boolean = {
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

case class AddOperatorFactory @Inject() (injector: Injector,
                                         val creator: CreateNode,
                                         val ai: Ai) extends CreateChildNodes {
  val neighbours: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[ValueRefFactory]))

  override def create(scope: Scope): Node = {
    val (updatedScope, leftChild) = creator.create(this, scope, ai)
    val (updatedScope2, rightChild) = creator.create(this, updatedScope, ai)
    AddOperator(left = leftChild,
      right = rightChild)
  }
}