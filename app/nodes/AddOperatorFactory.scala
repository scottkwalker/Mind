package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.IAi
import models.domain.scala.AddOperator


case class AddOperatorFactory @Inject()(injector: Injector,
                                        creator: ICreateNode,
                                        ai: IAi
                                         ) extends ICreateChildNodes with UpdateScopeNoChange {
  val neighbours: Seq[ICreateChildNodes] = Seq(injector.getInstance(classOf[ValueRefFactory]))

  override def create(scope: IScope): Node = {
    val (updatedScope, leftChild) = creator.create(legalNeighbours(scope, neighbours), scope, ai)
    val (_, rightChild) = creator.create(legalNeighbours(scope, neighbours), updatedScope, ai)
    AddOperator(left = leftChild,
      right = rightChild)
  }
}