package nodes

import nodes.helpers._
import com.google.inject.Inject

case class ValueRef(name: String) extends Node {
  override def toRawScala: String = name
  override def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) false else !name.isEmpty
}

case class ValueRefFactory @Inject() () extends CreateChildNodes {
  val neighbours: Seq[CreateChildNodes] = Seq() // No possible children TODO should this be Nil or throw an error if called

  override def canTerminateInStepsRemaining(scope: Scope) = if (scope.noStepsRemaining) false else true
  /*override val canTerminateInStepsRemaining: Scope => Boolean = {
    def inner(f: Scope => Boolean)(scope: Scope): Boolean = {
      if (scope.noStepsRemaining) false else true
    }
    Memoize.Y(inner)
  }*/

  override def create(scope: Scope): Node = {
    val name = "v" + scope.numVals
    ValueRef(name = name)
  }
}