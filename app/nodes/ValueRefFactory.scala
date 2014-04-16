package nodes

import nodes.helpers._
import com.google.inject.Inject
import ai.{IRandomNumberGenerator, IAi}
import models.domain.scala.ValueRef


case class ValueRefFactory @Inject()(ai: IAi,
                                     rng: IRandomNumberGenerator
                                      ) extends ICreateChildNodes with UpdateScopeNoChange {
  override val neighbours = Nil // No possible children

  override def canTerminateInStepsRemaining(scope: IScope): Boolean = scope.hasDepthRemaining

  override def create(scope: IScope): Node = {
    val name = "v" + ai.chooseIndex(scope.numVals)
    ValueRef(name = name)
  }
}