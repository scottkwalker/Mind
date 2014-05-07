package nodes

import nodes.helpers._
import com.google.inject.Inject
import ai.IAi
import models.domain.scala.IntegerM
import models.domain.common.Node


case class IntegerMFactory @Inject()(creator: ICreateSeqNodes,
                                     ai: IAi,
                                     populateMemoizationMapsStrategy: IPopulateMemoizationMaps
                                      ) extends CreateChildNodesImpl with UpdateScopeNoChange {
  override val neighbours = Nil // No possible children

  override def canTerminateInStepsRemaining(scope: IScope): Boolean = scope.hasDepthRemaining

  override def create(scope: IScope): Node = IntegerM()
}