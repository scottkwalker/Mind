package replaceEmpty

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.ValueRef
import utils.PozInt
import scala.async.Async.async
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class ValueRefFactoryImpl @Inject()(ai: SelectionStrategy) extends ValueRefFactory with UpdateScopeNoChange {

  override val nodesToChooseFrom = Set.empty[PozInt]

  override def create(scope: IScope): Future[Instruction] = async {
    ValueRef(index = ai.chooseIndex(scope.numVals))
  }
}