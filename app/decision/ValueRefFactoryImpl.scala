package decision

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.IScope
import models.domain.Step
import models.domain.scala.ValueRef
import utils.PozInt

import scala.async.Async.async
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class ValueRefFactoryImpl @Inject()(ai: SelectionStrategy) extends ValueRefFactory with UpdateScopeNoChange {

  override val nodesToChooseFrom = Set.empty[PozInt]

  override def createStep(scope: IScope): Future[Step] = async {
    ValueRef(index = ai.chooseIndex(scope.numVals))
  }

  override def createParams(scope: IScope): Future[AccumulateInstructions] = throw new RuntimeException("calling this method is not possible as there will be no params")

  override def createNodes(scope: IScope): Future[AccumulateInstructions] = throw new RuntimeException("calling this method is not possible as there will be no child nodes")
}