package replaceEmpty

import com.google.inject.Inject
import memoization.LookupNeighbours
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.NodeTree

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class NodeTreeFactoryImpl @Inject()(
                                          creator: CreateSeqNodes,
                                          legalNeighbours: LookupNeighbours
                                          ) extends NodeTreeFactory with UpdateScopeThrows {

  override val neighbourIds = Seq(ObjectDefFactoryImpl.id)

  // TODO is this ever going to be used for this type? Maybe we will only ever use the replaceEmpty method
  override def create(scope: IScope, premadeChildren: Seq[ReplaceEmpty]): Future[Instruction] = async {
    val generatedNodes = await(createNodes(scope))
    val fPremadeWithoutEmpties = premadeChildren.map(p => p.create(scope)) // TODO doesn't the scope need to be updated each pass
    val premadeWithoutEmpties = await(Future.sequence(fPremadeWithoutEmpties))
    val nodes = generatedNodes.instructions ++ premadeWithoutEmpties

    NodeTree(nodes)
  }

  override def createNodes(scope: IScope, acc: Seq[Instruction] = Seq.empty): Future[AccumulateInstructions] = {
    creator.create(
      possibleChildren = legalNeighbours.fetch(scope, neighbourIds),
      scope = scope,
      acc = acc,
      factoryLimit = scope.maxObjectsInTree
    )
  }

  override def create(scope: IScope): Future[Instruction] = async {
    val nodes = await(createNodes(scope))
    NodeTree(nodes.instructions)
  }
}

object NodeTreeFactoryImpl {

  val id = 4
}