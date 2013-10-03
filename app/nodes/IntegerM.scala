package nodes

import nodes.helpers.{Memoize, CreateChildNodes, CreateSeqNodes, Scope}
import com.google.inject.{Injector, Inject}
import ai.Ai

case class IntegerM() extends Node {
  override def toRawScala: String = "Int"
  override def validate(scope: Scope): Boolean = true
  override def replaceEmpty(scope: Scope, injector: Injector): Node = this
}

case class IntegerMFactory @Inject()(creator: CreateSeqNodes,
                                     ai: Ai) extends CreateChildNodes {
  val neighbours: Seq[CreateChildNodes] = Nil // No possible children

  override val canTerminateInStepsRemaining: Scope => Boolean = {
    def inner(f: Scope => Boolean)(scope: Scope): Boolean = scope.hasDepthRemaining
    Memoize.Y(inner)
  }

  override def create(scope: Scope): Node = {
    IntegerM()
  }
}