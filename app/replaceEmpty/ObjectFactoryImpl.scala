package replaceEmpty

import com.google.inject.Inject
import memoization.LookupChildren
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.Object
import scala.async.Async.{async, await}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class ObjectFactoryImpl @Inject()(
                                           creator: CreateSeqNodes,
                                           lookupChildren: LookupChildren
                                           ) extends ObjectFactory with UpdateScopeIncrementObjects {

  override val nodesToChooseFrom = Seq(FunctionMFactoryImpl.id)

  override def create(scope: IScope): Future[Instruction] = async {
    val nodesWithoutEmpties = await(createNodes(scope))

    Object(nodes = nodesWithoutEmpties.instructions,
      index = scope.numObjects)
  }

  override def createNodes(scope: IScope, acc: Seq[Instruction] = Seq.empty): Future[AccumulateInstructions] = {
    creator.create(
      possibleChildren = lookupChildren.fetch(scope, nodesToChooseFrom),
      scope = scope,
      acc = acc,
      factoryLimit = scope.maxFuncsInObject
    )
  }
}

object ObjectFactoryImpl {

  val id = 5
}