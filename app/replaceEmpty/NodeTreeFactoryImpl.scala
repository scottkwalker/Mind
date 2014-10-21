package replaceEmpty

import com.google.inject.Inject
import memoization.LookupNeighbours
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.NodeTree

case class NodeTreeFactoryImpl @Inject()(
                                          creator: CreateSeqNodes,
                                          legalNeighbours: LookupNeighbours
                                          ) extends NodeTreeFactory with UpdateScopeThrows {

  override val neighbourIds = Seq(ObjectDefFactoryImpl.id)

  def create(scope: IScope, premadeChildren: Seq[ReplaceEmpty]): Instruction = {
    val (_, generated) = createNodes(scope)
    val nodes = generated ++ premadeChildren.map(p => p.create(scope))

    NodeTree(nodes)
  }

  def createNodes(scope: IScope, acc: Seq[Instruction] = Seq()): (IScope, Seq[Instruction]) = {
    creator.create(
      possibleChildren = legalNeighbours.fetch(scope, neighbourIds),
      scope = scope,
      saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumFuncs(accLength)),
      acc = acc,
      factoryLimit = scope.maxObjectsInTree
    )
  }

  override def create(scope: IScope): Instruction = {
    val (_, nodes) = createNodes(scope)
    NodeTree(nodes)
  }
}

object NodeTreeFactoryImpl {

  val id = 4
}