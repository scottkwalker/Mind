package replaceEmpty

import com.google.inject.Inject
import memoization.LookupNeighbours
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.AddOperator
import utils.Timeout.finiteTimeout
import scala.concurrent.Await

case class AddOperatorFactoryImpl @Inject()(
                                             creator: CreateNode,
                                             legalNeighbours: LookupNeighbours
                                             ) extends AddOperatorFactory with UpdateScopeNoChange {

  override val neighbourIds = Seq(ValueRefFactoryImpl.id)

  override def create(scope: IScope): Instruction = {
    val ln = legalNeighbours.fetch(scope, neighbourIds)
    val (updatedScope, leftChild) = Await.result(creator.create(ln, scope), finiteTimeout)
    val (_, rightChild) = Await.result(creator.create(ln, updatedScope), finiteTimeout)
    AddOperator(left = leftChild,
      right = rightChild)
  }
}

object AddOperatorFactoryImpl {

  val id = 1
}