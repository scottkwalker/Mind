package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.IAi
import models.domain.scala.IntegerM


case class IntegerMFactory @Inject()(creator: ICreateSeqNodes,
                                     ai: IAi,
                                     populateMemoizationMapsStrategy: IPopulateMemoizationMaps
                                      ) extends ICreateChildNodes with UpdateScopeNoChange {
  val neighbours: Seq[ICreateChildNodes] = Nil // No possible children

  override val canTerminateInStepsRemaining: IScope => Boolean = {
    def inner(f: IScope => Boolean)(scope: IScope): Boolean = scope.hasDepthRemaining
    Memoize.Y(inner)
  }

  override def create(scope: IScope): Node = {
    IntegerM()
  }
}