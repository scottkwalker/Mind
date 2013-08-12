package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope

case class ValueM(val name: String) extends Node {
  def toRawScala: String = name
  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else !name.isEmpty
}

case object ValueM extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq()

  override def couldTerminate(stepsRemaining: Integer) = {
    if (stepsRemaining == 0) false else true
  }

  def create(scope: Option[Scope]): Node =
    scope match {
      case Some(s: Scope) => ValueM(name = "v" + s.numVals)
      case _ => ValueM("test create ValueM")
    }
}