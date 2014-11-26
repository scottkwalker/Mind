package replaceEmpty

import com.google.inject.Inject
import memoization.LookupNeighbours
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.AddOperator
import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AddOperatorFactoryImpl @Inject()(
                                             creator: CreateNode,
                                             legalNeighbours: LookupNeighbours
                                             ) extends AddOperatorFactory with UpdateScopeNoChange {

  override val neighbourIds = Seq(ValueRefFactoryImpl.id)

  override def create(scope: IScope): Future[Instruction] = async {
    val possibleNodes = legalNeighbours.fetch(scope, neighbourIds)
    val (updatedScope, left) = await(creator.create(possibleNodes, scope))
    val (_, right) = await(creator.create(possibleNodes, updatedScope))
    AddOperator(left = left,
      right = right)
  }
}

object AddOperatorFactoryImpl {

  val id = 1
}