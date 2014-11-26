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

  override def create(scope: IScope, premadeChildren: Seq[ReplaceEmpty]): Future[Instruction] = async {
    val (_, generated) = await(createNodes(scope))
    val premadeWithoutEmptyChildren = premadeChildren.map(p => p.create(scope)) // TODO doesn't the scope need to be updated each pass
    val f = await(Future.sequence(premadeWithoutEmptyChildren))
    val nodes = generated ++ f

    NodeTree(nodes)
  }

  override def createNodes(scope: IScope, acc: Seq[Instruction] = Seq()): Future[(IScope, Seq[Instruction])] = {
    creator.create(
      possibleChildren = legalNeighbours.fetch(scope, neighbourIds),
      scope = scope,
      acc = acc,
      factoryLimit = scope.maxObjectsInTree
    )
  }

  override def create(scope: IScope): Future[Instruction] = async {
    val (_, nodes) = await(createNodes(scope))
    NodeTree(nodes)
  }
}

object NodeTreeFactoryImpl {

  val id = 4
}