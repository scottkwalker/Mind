package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope

case class FunctionM(val nodes: Seq[Node], val name: String = "f0") extends Node {
  def toRawScala: String = s"def ${name}${params.mkString("(", ", ", ")")} = ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  val params: Seq[String] = Seq("a: Int", "b: Int")

  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) { false }
  else {
    !name.isEmpty && 
    nodes.forall(_ match {
      case n: AddOperator => n.validate(stepsRemaining - 1)
      case n: ValueM => n.validate(stepsRemaining - 1)
      case _: Empty => false
      case _ => false
    })
  }
}

case object FunctionM extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(AddOperator, ValueM)

  def create(scope: Option[Scope]): Node = scope match {
    case Some(s: Scope) => FunctionM(Seq(allPossibleChildren(0).create(scope)), name = "f" + s.numFuncs)
    case _ => FunctionM(Nil, "test create Method")
  }
}