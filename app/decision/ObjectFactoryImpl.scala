package decision

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.IScope
import models.domain.Step
import models.domain.scala.Object

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ObjectFactoryImpl @Inject()(
                                        creator: CreateSeqNodes,
                                        lookupChildren: LookupChildrenWithFutures
                                        ) extends ObjectFactory with UpdateScopeIncrementObjects {

  override val nodesToChooseFrom = Set(FunctionMFactory.id)

  override def createStep(scope: IScope): Future[Step] = async {
    val nodesWithoutEmpties = await(createNodes(scope))

    Object(nodes = nodesWithoutEmpties.instructions,
      index = scope.numObjects)
  }

  override def createNodes(scope: IScope, acc: Seq[Step] = Seq.empty): Future[AccumulateInstructions] = {
    creator.create(
      possibleChildren = lookupChildren.get(scope, nodesToChooseFrom),
      scope = scope,
      acc = acc,
      factoryLimit = scope.maxFuncsInObject
    )
  }
}