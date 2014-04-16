package nodes

import nodes.helpers._
import com.google.inject.Inject
import ai.IAi
import models.domain.scala.IntegerM


case class IntegerMFactory @Inject()(creator: ICreateSeqNodes,
                                     ai: IAi,
                                     populateMemoizationMapsStrategy: IPopulateMemoizationMaps
                                      ) extends ICreateChildNodes with UpdateScopeNoChange {
  override val neighbours = Nil // No possible children

  override def canTerminateInStepsRemaining(scope: IScope): Boolean = scope.hasDepthRemaining

  override def create(scope: IScope): Node = IntegerM()
}