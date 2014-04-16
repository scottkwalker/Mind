package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.{IRandomNumberGenerator, IAi}
import models.domain.scala.ValueRef


case class ValueRefFactory @Inject()(ai: IAi,
                                     rng: IRandomNumberGenerator
                                      ) extends ICreateChildNodes with UpdateScopeNoChange {
  val neighbours: Seq[ICreateChildNodes] = Nil // No possible children

  override val canTerminateInStepsRemaining: IScope => Boolean = {
    def inner(f: IScope => Boolean)(scope: IScope): Boolean = scope.hasDepthRemaining
    Memoize.Y(inner)
  }

  override def create(scope: IScope): Node = {
    val name = "v" + ai.chooseIndex(scope.numVals)
    ValueRef(name = name)
  }
}