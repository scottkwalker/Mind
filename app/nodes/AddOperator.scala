package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai

case class AddOperator(left: Node, right: Node) extends Node with UpdateScopeNoChange {
  override def toRawScala: String = s"${left.toRawScala} + ${right.toRawScala}"

  override def validate(scope: Scope): Boolean = {
    if (scope.hasDepthRemaining) validate(left, scope) && validate(right, scope)
    else false
  }

  private def validate(n: Node, scope: Scope) = {
    n match {
      case _: ValueRef => n.validate(scope.incrementDepth)
      case _: Empty => false
      case _ => false
    }
  }

  override def replaceEmpty(scope: Scope, injector: Injector): Node = {
    val l = replaceEmpty(scope, injector, left)
    val r = replaceEmpty(scope, injector, right)
    AddOperator(l, r)
  }

  private def replaceEmpty(scope: Scope, injector: Injector, n: Node): Node = {
    n match {
      case _: Empty => injector.getInstance(classOf[ValueRefFactory]).create(scope)
      case n: Node => n.replaceEmpty(scope.incrementDepth, injector)
    }
  }

  override def getMaxDepth: Int = 1 + math.max(left.getMaxDepth, right.getMaxDepth)
}


case class AddOperatorFactory @Inject()(injector: Injector,
                                        creator: CreateNode,
                                        ai: Ai,
                                        memoizeCanTerminateInStepsRemaining: MemoizeDi) extends CreateChildNodes with UpdateScopeNoChange {
  val neighbours: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[ValueRefFactory]))

  override def create(scope: Scope): Node = {
    val (updatedScope, leftChild) = creator.create(legalNeighbours(scope), scope, ai)
    val (_, rightChild) = creator.create(legalNeighbours(scope), updatedScope, ai)
    AddOperator(left = leftChild,
      right = rightChild)
  }
}