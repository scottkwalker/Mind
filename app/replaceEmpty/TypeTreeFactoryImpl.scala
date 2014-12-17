package replaceEmpty

import com.google.inject.Inject
import memoization.LookupChildren
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.TypeTree

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TypeTreeFactoryImpl @Inject()(
                                          creator: CreateSeqNodes,
                                          lookupChildren: LookupChildren
                                          ) extends TypeTreeFactory with UpdateScopeThrows {

  override val nodesToChooseFrom = Set(ObjectFactoryImpl.id)

  // TODO is this ever going to be used for this type? Maybe we will only ever use the replaceEmpty method
  override def create(scope: IScope, premadeChildren: Seq[ReplaceEmpty]): Future[Instruction] = async {
    val generatedNodes = await(createNodes(scope))
    val fPremadeWithoutEmpties = premadeChildren.map(p => p.create(scope)) // TODO doesn't the scope need to be updated each pass
    val premadeWithoutEmpties = await(Future.sequence(fPremadeWithoutEmpties))
    val nodes = generatedNodes.instructions ++ premadeWithoutEmpties

    TypeTree(nodes)
  }

  override def createNodes(scope: IScope, acc: Seq[Instruction] = Seq.empty): Future[AccumulateInstructions] = {
    creator.create(
      possibleChildren = lookupChildren.fetch(scope, nodesToChooseFrom.toSeq),
      scope = scope,
      acc = acc,
      factoryLimit = scope.maxObjectsInTree
    )
  }

  override def create(scope: IScope): Future[Instruction] = async {
    val nodes = await(createNodes(scope))
    TypeTree(nodes.instructions)
  }
}

object TypeTreeFactoryImpl {

  val id = 4
}