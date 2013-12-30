package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.Ai
import scala.util.Random

case class ValueRef(name: String) extends Node with UpdateScopeNoChange {
  override def toRawScala: String = name

  override def validate(scope: IScope): Boolean = if (scope.hasDepthRemaining) !name.isEmpty else false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = this

  override def getMaxDepth = 1
}

case class ValueRefFactory @Inject()(ai: Ai,
                                     rng: Random,
                                     memoizeCanTerminateInStepsRemaining: MemoizeDi) extends ICreateChildNodes with UpdateScopeNoChange {
  val neighbours: Seq[ICreateChildNodes] = Nil // No possible children

  override def canTerminateInStepsRemaining(scope: IScope): Boolean = {
    val result = scope.hasDepthRemaining
    memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
  }

  override def create(scope: IScope): Node = {
    val name = "v" + ai.chooseIndex(scope.numVals)
    ValueRef(name = name)
  }
}