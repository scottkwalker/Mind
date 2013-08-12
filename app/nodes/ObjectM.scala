package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope

case class ObjectM(val nodes: Seq[Node]) extends Node {
  def toRawScala: String = s"object ${name} ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  val name = "Individual"

  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else nodes.forall(n => n match {
    case _: FunctionM => n.validate(stepsRemaining - 1)
    case _: Empty => false
    case _ => false
  })
}

case object ObjectM extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(FunctionM)

  def create(scope: Option[Scope]): Node = ObjectM(Seq(allPossibleChildren(0).create(scope)))
}