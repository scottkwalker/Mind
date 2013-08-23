package nodes

import nodes.helpers._
import com.google.inject.Guice
import com.tzavellas.sse.guice.ScalaModule
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai

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

  override def create(scope: Scope): Node = {
    val ai = injector.getInstance(classOf[Ai])
    
    val leftChildFactory = ai.chooseChild(this, scope)
    val updatedScope1 = leftChildFactory.updateScope(scope)
    val leftChild = leftChildFactory.create(updatedScope1)
    
    val rightChildFactory = ai.chooseChild(this, scope)
    val updatedScope2 = leftChildFactory.updateScope(updatedScope1)
    val rightChild = rightChildFactory.create(updatedScope2)
    
    AddOperator(left = leftChild, right = rightChild)
  }
}