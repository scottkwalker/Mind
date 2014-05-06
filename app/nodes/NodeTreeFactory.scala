package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.{IRandomNumberGenerator, IAi}
import models.domain.scala.NodeTree

case class NodeTreeFactory @Inject()(injector: Injector,
                                     creator: ICreateSeqNodes,
                                     ai: IAi,
                                     rng: IRandomNumberGenerator
                                      ) extends ICreateChildNodes with UpdateScopeThrows {
  override val neighbours = Seq(injector.getInstance(classOf[ObjectDefFactory]))

  override def create(scope: IScope, premadeChildren: Seq[ICreateChildNodes]): Node = {
    val (_, generated) = createNodes(scope)
    val nodes: Seq[Node] = generated ++ premadeChildren.map(p => p.create(scope))

    NodeTree(nodes)
  }

  override def create(scope: IScope): Node = {
    val (_, nodes) = createNodes(scope)
    NodeTree(nodes)
  }

  def createNodes(scope: IScope, acc: Seq[Node] = Seq()): (IScope, Seq[Node]) = creator.createSeq(
    possibleChildren = legalNeighbours(scope, neighbours),
    scope = scope,
    saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumFuncs(accLength)),
    acc = acc,
    factoryLimit = scope.maxObjectsInTree
  )
}