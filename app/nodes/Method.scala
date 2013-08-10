package nodes

case class Method(val nodes: Seq[Node]) extends Node {
  def canTerminate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else nodes.forall(f => f.canTerminate(stepsRemaining - 1))
  def toRawScala: String = s"def ${name}${params.mkString("(", ", ", ")")} = ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  val name = "f1"
  val params: Seq[String] = Seq("a: Int", "b: Int")

  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else nodes.forall(n => n match {
    case _: AddOperator => n.validate(stepsRemaining - 1)
    case _: Empty => false
    case _ => false
  })
}

case object Method extends FindValidChildren {
  val allPossibleChildren = Seq(AddOperator, ValueM)
}

