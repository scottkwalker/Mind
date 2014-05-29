package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.IAi
import models.domain.scala.AddOperator
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours

trait AddOperatorFactory extends ICreateChildNodes

case class AddOperatorFactoryImpl @Inject()(injector: Injector,
                                        creator: ICreateNode,
                                        legalNeighbours: LegalNeighbours
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