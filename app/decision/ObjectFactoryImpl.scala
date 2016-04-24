package decision

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.IScope
import models.domain.Step
import models.domain.scala.ObjectImpl

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ObjectFactoryImpl @Inject() (
    creator: CreateSeqNodes,
    lookupChildren: LookupChildrenWithFutures) extends ObjectFactory with UpdateScopeIncrementObjects {

  override val nodesToChooseFrom = Set(FunctionMFactory.id)

  override def fillEmptySteps(scope: IScope): Future[Step] =
    createNodes(scope).map { nodesWithoutEmpties =>
      ObjectImpl(nodes = nodesWithoutEmpties.instructions,
        index = scope.numObjects)
    }

  override def createNodes(scope: IScope): Future[AccumulateInstructions] = {
    val acc: Seq[Step] = Seq.empty
    creator.create(
      possibleChildren = lookupChildren.get(scope, nodesToChooseFrom),
      scope = scope,
      acc = acc,
      factoryLimit = scope.maxFuncsInObject
    )
  }

  override def createParams(scope: IScope): Future[AccumulateInstructions] = throw new RuntimeException("calling this method is not possible as there will be no params")
}