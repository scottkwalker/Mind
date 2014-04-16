package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.{IRandomNumberGenerator, IAi}
import models.domain.scala.ObjectDef


case class ObjectDefFactory @Inject()(injector: Injector,
                                      creator: ICreateSeqNodes,
                                      ai: IAi,
                                      rng: IRandomNumberGenerator
                                       ) extends ICreateChildNodes with UpdateScopeIncrementObjects {
  val neighbours: Seq[ICreateChildNodes] = Seq(injector.getInstance(classOf[FunctionMFactory]))

  override def create(scope: IScope): Node = {
    val (_, nodes) = createNodes(scope)

    ObjectDef(nodes = nodes,
      name = "o" + scope.numObjects)
  }

  def createNodes(scope: IScope, acc: Seq[Node] = Seq()) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumFuncs(accLength)),
    acc = acc,
    factoryLimit = scope.maxFuncsInObject
  )
}