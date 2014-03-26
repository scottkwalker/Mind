package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.{IRandomNumberGenerator, IAi}

case class ValueRef(name: String) extends Node with UpdateScopeNoChange {
  override def toRawScala: String = name

  override def validate(scope: IScope): Boolean = if (scope.hasDepthRemaining) !name.isEmpty else false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = this

  override def getMaxDepth = 1
}

case class ValueRefFactory @Inject()(ai: IAi,
                                     rng: IRandomNumberGenerator,
                                     mapOfLegalNeigbours: IMemoizeDi[IScope, Seq[ICreateChildNodes]],
                                     mapOfCanTerminateInStepsRemaining: IMemoizeDi[IScope, Boolean]) extends ICreateChildNodes with UpdateScopeNoChange {
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