package nodes

import com.google.inject.Inject
import models.domain.common.Node
import models.domain.scala.ObjectDef
import nodes.helpers._
import nodes.legalNeighbours.LegalNeighbours

trait ObjectDefFactory extends ICreateChildNodes

case class ObjectDefFactoryImpl @Inject()(
                                           creator: ICreateSeqNodes,
                                           legalNeighbours: LegalNeighbours
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

  final val id = 5
}