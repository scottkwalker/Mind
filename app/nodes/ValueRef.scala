package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}

case class ValueRef(name: String) extends Node {
  override def toRawScala: String = name
  override def validate(scope: Scope): Boolean = if (scope.hasDepthRemaining) !name.isEmpty else false
  override def replaceEmpty(scope: Scope, injector: Injector): Node = this
  override def getMaxDepth = 1
}

case class ValueRefFactory @Inject() () extends CreateChildNodes with ValueRefUpdateScope {
  val neighbours: Seq[CreateChildNodes] = Nil // No possible children

  override val canTerminateInStepsRemaining: Scope => Boolean = {
    def inner(f: Scope => Boolean)(scope: Scope): Boolean = scope.hasDepthRemaining
    Memoize.Y(inner)
  }

  override def create(scope: Scope): Node = {
    val name = "v" + scope.numVals
    ValueRef(name = name)
  }
}

trait ValueRefUpdateScope extends UpdateScope {
  override def updateScope(scope: Scope): Scope = scope
}