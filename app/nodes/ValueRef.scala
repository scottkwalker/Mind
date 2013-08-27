package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope
import com.google.inject.Inject

case class ValueRef(val name: String) extends Node {
  override def toRawScala: String = name
  override def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) false else !name.isEmpty
}

case class ValueRefFactory @Inject() () extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq() // No possible children

  override def couldTerminate(scope: Scope) = {
    if (scope.noStepsRemaining) false else true
  }

  override def create(scope: Scope): Node = {
    val name = "v" + scope.numVals
    ValueRef(name = name)
  }
}