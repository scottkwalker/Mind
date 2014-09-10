package factory

import com.google.inject.Inject
import memoization.LegalNeighboursMemo
import models.common.IScope
import models.domain.Node
import models.domain.scala.AddOperator

case class AddOperatorFactoryImpl @Inject()(
                                             creator: CreateNode,
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

  val id = 1
}