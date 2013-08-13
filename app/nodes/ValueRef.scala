package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope

case class ValueRef(val name: String) extends Node {
  def toRawScala: String = name
  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else !name.isEmpty
}

case object ValueRef extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq()

  override def couldTerminate(stepsRemaining: Integer) = {
    if (stepsRemaining == 0) false else true
  }

  def create(scope: Scope): Node = ValueRef(name = "v" + scope.numVals)
}