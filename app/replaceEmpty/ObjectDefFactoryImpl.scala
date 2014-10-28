package replaceEmpty

import com.google.inject.Inject
import memoization.LookupNeighbours
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.ObjectDef
import scala.concurrent.Await

case class ObjectDefFactoryImpl @Inject()(
                                           creator: CreateSeqNodes,
                                           legalNeighbours: LookupNeighbours
                                           ) extends ObjectDefFactory with UpdateScopeIncrementObjects {

  override val neighbourIds = Seq(FunctionMFactoryImpl.id)

  override def create(scope: IScope): Instruction = {
    val (_, nodes) = createNodes(scope)

    ObjectDef(nodes = nodes,
      index = scope.numObjects)
  }

  def createNodes(scope: IScope, acc: Seq[Instruction] = Seq()) = {
    creator.create(
      possibleChildren = Await.result(legalNeighbours.fetch(scope, neighbourIds), finiteTimeout),
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