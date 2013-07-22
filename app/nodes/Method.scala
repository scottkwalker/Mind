package nodes

case class Method(val nodes: Seq[Node]) extends Node {
  def canTerminate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else nodes.forall(f => f.canTerminate(stepsRemaining - 1))
  def toRawScala: String = s"def ${name}${params.mkString("(", ", ", ")")} = ${nodes.map(f=>f.toRawScala).mkString("{ ", " ", " }")}"
  val name = "f1"
  val params: Seq[String] = Seq("a: Int", "b: Int")
  def validate = nodes.forall(f => f.validate)
}