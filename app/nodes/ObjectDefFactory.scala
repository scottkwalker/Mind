package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.{IRandomNumberGenerator, IAi}
import models.domain.scala.ObjectDef
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours


case class ObjectDefFactory @Inject()(injector: Injector,
                                      creator: ICreateSeqNodes,
                                      ai: IAi,
                                      rng: IRandomNumberGenerator,
                                      legalNeighbours: LegalNeighbours
                                       ) extends ICreateChildNodes with UpdateScopeIncrementObjects {
  override val neighbours = Seq(injector.getInstance(classOf[FunctionMFactory]))
  override val neighbours2 = Seq(FunctionMFactory.id)

  override def create(scope: IScope): Node = {
    val (_, nodes) = createNodes(scope)

    ObjectDef(nodes = nodes,
      name = "o" + scope.numObjects)
  }

  def createNodes(scope: IScope, acc: Seq[Node] = Seq()) = creator.createSeq(
    possibleChildren = legalNeighbours.fetch(scope, neighbours),
    scope = scope,
    saveAccLengthInScope = Some((s: IScope, accLength: Int) => s.setNumFuncs(accLength)),
    acc = acc,
    factoryLimit = scope.maxFuncsInObject
  )
}

object ObjectDefFactory {
  val id = 5
}