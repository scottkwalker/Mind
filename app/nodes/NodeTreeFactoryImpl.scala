package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import models.domain.scala.NodeTree
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours

trait NodeTreeFactory extends ICreateChildNodes

case class NodeTreeFactoryImpl @Inject()(injector: Injector,
                                     creator: ICreateSeqNodes,
                                     legalNeighbours: LegalNeighbours
                                      ) extends NodeTreeFactory with UpdateScopeThrows {
  override val neighbourIds = Seq(ObjectDefFactoryImpl.id)

  def create(scope: IScope, premadeChildren: Seq[ICreateChildNodes]): Node = {
    val (_, generated) = createNodes(scope)
    val nodes = generated ++ premadeChildren.map(p => p.create(scope))

    NodeTree(nodes)
  }

  override def create(scope: IScope): Node = {
    val (_, nodes) = createNodes(scope)
    NodeTree(nodes)
  }

  def createNodes(scope: IScope, acc: Seq[Node] = Seq()): (IScope, Seq[Node]) = {
    creator.createSeq(
      possibleChildren = legalNeighbours.fetch(scope, neighbourIds),
      scope = scope,
      saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumFuncs(accLength)),
      acc = acc,
      factoryLimit = scope.maxObjectsInTree
    )
  }
}

object NodeTreeFactoryImpl {
  val id = 4
}