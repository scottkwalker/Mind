package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.IAi
import models.domain.scala.AddOperator
import models.domain.common.Node


case class AddOperatorFactory @Inject()(injector: Injector,
                                        creator: ICreateNode,
                                        ai: IAi
                                         ) extends ICreateChildNodes with UpdateScopeNoChange {
  override val neighbours = Seq(injector.getInstance(classOf[ValueRefFactory]))

  override def create(scope: IScope): Node = {
    val ln = legalNeighbours(scope, neighbours)
    val (updatedScope, leftChild) = creator.create(ln, scope, ai)
    val (_, rightChild) = creator.create(ln, updatedScope, ai)
    AddOperator(left = leftChild,
      right = rightChild)
  }
}