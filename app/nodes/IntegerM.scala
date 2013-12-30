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

case class IntegerMFactory @Inject()(creator: CreateSeqNodes,
                                     ai: IAi,
                                     memoizeLegalNeigbours: MemoizeDi[IScope, Seq[ICreateChildNodes]],
                                     memoizeCanTerminateInStepsRemaining: MemoizeDi[IScope, Boolean]) extends ICreateChildNodes with UpdateScopeNoChange {
  val neighbours: Seq[ICreateChildNodes] = Nil // No possible children

  /*
  override val canTerminateInStepsRemaining: IScope => Boolean = {
    def inner(f: IScope => Boolean)(scope: IScope): Boolean = scope.hasDepthRemaining
    Memoize.Y(inner)
  }*/
  override def canTerminateInStepsRemaining(scope: IScope): Boolean = {
    val result = scope.hasDepthRemaining
    memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
  }

  override def create(scope: IScope): Node = {
    IntegerM()
  }
}