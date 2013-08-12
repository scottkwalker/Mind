package nodes

case class Method(val nodes: Seq[Node]) extends Node {
  def toRawScala: String = s"def ${name}${params.mkString("(", ", ", ")")} = ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  val name = "f1"
  val params: Seq[String] = Seq("a: Int", "b: Int")

  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else nodes.forall(n => n match {
    case _: AddOperator => n.validate(stepsRemaining - 1)
    case _: Empty => false
    case _ => false
  })
}

case object Method extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(AddOperator, ValueM)

  def create: Node = Method(Seq(allPossibleChildren(0).create))
}