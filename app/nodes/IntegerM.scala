package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.IAi
import nodes.helpers.CreateSeqNodes

case class IntegerM() extends Node with UpdateScopeNoChange {
  override def toRawScala: String = "Int"

  override def validate(scope: IScope): Boolean = true

  override def replaceEmpty(scope: IScope, injector: Injector): Node = this

  override def getMaxDepth = 1
}

case class IntegerMFactory @Inject()(creator: ICreateSeqNodes,
                                     ai: IAi,
                                     populateMemoizationMapsStrategy: IPopulateMemoizationMaps,
                                     mapOfCanTerminateInStepsRemaining: IMemoizeDi[IScope, Boolean],
                                     mapOfLegalNeigbours: IMemoizeDi[IScope, Seq[ICreateChildNodes]]
                                     ) extends ICreateChildNodes with UpdateScopeNoChange {
  val neighbours: Seq[ICreateChildNodes] = Nil // No possible children

  override def canTerminateInStepsRemaining(scope: IScope): Boolean = {
    val result = scope.hasDepthRemaining
    mapOfCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
  }

  override def create(scope: IScope): Node = {
    IntegerM()
  }

  override def populateMemoizationMaps(): Unit = {
    populateMemoizationMapsStrategy.memoizeCanTerminateInStepsRemaining(mapOfCanTerminateInStepsRemaining,
      this)
  }
}