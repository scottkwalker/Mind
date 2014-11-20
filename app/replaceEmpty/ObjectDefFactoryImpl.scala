package replaceEmpty

import com.google.inject.Inject
import memoization.LookupNeighbours
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.ObjectDef
import scala.async.Async.{async, await}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class ObjectDefFactoryImpl @Inject()(
                                           creator: CreateSeqNodes,
                                           legalNeighbours: LookupNeighbours
                                           ) extends ObjectDefFactory with UpdateScopeIncrementObjects {

  override val neighbourIds = Seq(FunctionMFactoryImpl.id)

  override def create(scope: IScope): Future[Instruction] = async {
    val (_, nodes) = await(createNodes(scope))

    ObjectDef(nodes = nodes,
      index = scope.numObjects)
  }

  override def createNodes(scope: IScope, acc: Seq[Instruction] = Seq()): Future[(IScope, Seq[Instruction])] = {
    creator.create(
      possibleChildren = legalNeighbours.fetch(scope, neighbourIds),
      scope = scope,
      saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumFuncs(accLength)),
      acc = acc,
      factoryLimit = scope.maxFuncsInObject
    )
  }
}

object ObjectDefFactoryImpl {

  val id = 5
}