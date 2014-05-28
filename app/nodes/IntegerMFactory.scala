package nodes

import nodes.helpers._
import com.google.inject.Inject
import ai.IAi
import models.domain.scala.IntegerM
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours

case class IntegerMFactory @Inject()(creator: ICreateSeqNodes,
                                     ai: IAi,
                                     populateMemoizationMapsStrategy: IPopulateMemoizationMaps
                                      ) extends ICreateChildNodes with UpdateScopeNoChange {
  override val neighbours = Seq.empty // No possible children
  override val neighbours2 = Seq.empty

  override def create(scope: IScope): Node = IntegerM()
}

object IntegerMFactory {
  val id = 3
}