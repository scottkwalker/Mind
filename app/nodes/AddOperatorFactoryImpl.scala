package nodes

import com.google.inject.Inject
import models.common.{IScope, Node}
import models.domain.scala.AddOperator
import nodes.helpers._
import nodes.legalNeighbours.LegalNeighboursMemo

trait AddOperatorFactory extends ICreateChildNodes

case class AddOperatorFactoryImpl @Inject()(
                                             creator: ICreateNode,
                                             legalNeighbours: LegalNeighboursMemo
                                             ) extends AddOperatorFactory with UpdateScopeNoChange {

  override val neighbourIds = Seq(ValueRefFactoryImpl.id)

  override def create(scope: IScope): Node = {
    val ln = legalNeighbours.fetch(scope, neighbourIds)
    val (updatedScope, leftChild) = creator.create(ln, scope)
    val (_, rightChild) = creator.create(ln, updatedScope)
    AddOperator(left = leftChild,
      right = rightChild)
  }
}

object AddOperatorFactoryImpl {

  final val id = 1
}