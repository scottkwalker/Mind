package nodes

import nodes.helpers.{Memoize, CreateChildNodes, CreateSeqNodes, Scope}
import com.google.inject.{Injector, Inject}
import ai.Ai

case class IntegerM() extends Node with UpdateScopeNoChange {
  override def toRawScala: String = "Int"

  override def validate(scope: Scope): Boolean = true

  override def replaceEmpty(scope: Scope, injector: Injector): Node = this

  override def getMaxDepth = 1
}

case class IntegerMFactory @Inject()(creator: CreateSeqNodes,
                                     ai: Ai) extends CreateChildNodes with UpdateScopeNoChange {
  val neighbours: Seq[CreateChildNodes] = Nil // No possible children

  override val canTerminateInStepsRemaining: Scope => Boolean = {
    def inner(f: Scope => Boolean)(scope: Scope): Boolean = scope.hasDepthRemaining
    Memoize.Y(inner)
  }

  override def create(scope: Scope): Node = {
    IntegerM()
  }
}