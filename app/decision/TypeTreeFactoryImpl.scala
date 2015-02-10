package decision

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.IScope
import models.domain.Step
import models.domain.scala.TypeTree

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TypeTreeFactoryImpl @Inject()(
                                          creator: CreateSeqNodes,
                                          lookupChildren: LookupChildrenWithFutures
                                          ) extends TypeTreeFactory with UpdateScopeThrows {

  override val nodesToChooseFrom = Set(ObjectFactory.id)

  override def create(scope: IScope, premadeChildren: Seq[Decision]): Future[Step] = async {
    val generatedNodes = await(createNodes(scope))
    val fPremadeWithoutEmpties = premadeChildren.map(p => p.createStep(scope)) // TODO doesn't the scope need to be updated each pass
    val premadeWithoutEmpties = await(Future.sequence(fPremadeWithoutEmpties))
    val nodes = generatedNodes.instructions ++ premadeWithoutEmpties

    TypeTree(nodes)
  }

  override def createNodes(scope: IScope): Future[AccumulateInstructions] = {
    val acc: Seq[Step] = Seq.empty
    creator.create(
      possibleChildren = lookupChildren.get(scope, nodesToChooseFrom),
      scope = scope,
      acc = acc,
      factoryLimit = scope.maxObjectsInTree
    )
  }

  override def createStep(scope: IScope): Future[Step] = async {
    val nodes = await(createNodes(scope))
    TypeTree(nodes.instructions)
  }

  override def createParams(scope: IScope): Future[AccumulateInstructions] = throw new RuntimeException("calling this method is not possible as there will be no params")
}