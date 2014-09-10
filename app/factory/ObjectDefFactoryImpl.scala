package factory

import com.google.inject.Inject
import memoization.LegalNeighboursMemo
import models.common.IScope
import models.domain.Node
import models.domain.scala.ObjectDef

case class ObjectDefFactoryImpl @Inject()(
                                           creator: CreateSeqNodes,
                                           legalNeighbours: LegalNeighboursMemo
                                           ) extends ObjectDefFactory with UpdateScopeIncrementObjects {

  override val neighbourIds = Seq(FunctionMFactoryImpl.id)

  override def create(scope: IScope): Node = {
    val (_, nodes) = createNodes(scope)

    ObjectDef(nodes = nodes,
      name = "o" + scope.numObjects)
  }

  def createNodes(scope: IScope, acc: Seq[Node] = Seq()) = {
    creator.createSeq(
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