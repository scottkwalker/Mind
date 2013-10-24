package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.Ai
import scala.util.Random

case class ValueRef(name: String) extends Node with UpdateScopeNoChange {
  override def toRawScala: String = name

  override def validate(scope: Scope): Boolean = if (scope.hasDepthRemaining) !name.isEmpty else false

  override def replaceEmpty(scope: Scope, injector: Injector): Node = this

  override def getMaxDepth = 1
}

case class ValueRefFactory @Inject()(ai: Ai,
                                     rng: Random) extends CreateChildNodes with UpdateScopeNoChange {
  val neighbours: Seq[CreateChildNodes] = Nil // No possible children

  override val canTerminateInStepsRemaining: Scope => Boolean = {
    def inner(f: Scope => Boolean)(scope: Scope): Boolean = scope.hasDepthRemaining
    Memoize.Y(inner)
  }

  override def create(scope: Scope): Node = {
    val name = "v" + ai.chooseIndex(scope.numVals)
    ValueRef(name = name)
  }
}