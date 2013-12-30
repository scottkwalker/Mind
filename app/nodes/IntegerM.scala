package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.Ai
import nodes.helpers.CreateSeqNodes
import nodes.helpers.Scope

case class IntegerM() extends Node with UpdateScopeNoChange {
  override def toRawScala: String = "Int"

  override def validate(scope: IScope): Boolean = true

  override def replaceEmpty(scope: IScope, injector: Injector): Node = this

  override def getMaxDepth = 1
}

case class IntegerMFactory @Inject()(creator: CreateSeqNodes,
                                     ai: Ai,
                                     memoizeCanTerminateInStepsRemaining: MemoizeDi) extends CreateChildNodes with UpdateScopeNoChange {
  val neighbours: Seq[CreateChildNodes] = Nil // No possible children

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