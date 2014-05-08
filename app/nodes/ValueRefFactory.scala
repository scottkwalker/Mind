package nodes

import nodes.helpers._
import com.google.inject.Inject
import ai.{IRandomNumberGenerator, IAi}
import models.domain.scala.ValueRef
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours


case class ValueRefFactory @Inject()(ai: IAi,
                                     rng: IRandomNumberGenerator,
                                     legalNeighbours: LegalNeighbours
                                      ) extends CreateChildNodesImpl with UpdateScopeNoChange {
  override val neighbours = Nil // No possible children

  override def canTerminateInStepsRemaining(scope: IScope): Boolean = scope.hasDepthRemaining

  override def create(scope: IScope): Node = {
    val name = "v" + ai.chooseIndex(scope.numVals)
    ValueRef(name = name)
  }
}