package nodes

case class Method() extends Node {
  def isTerminal: Boolean = false
  def canTerminate(stepsRemaining: Integer): Boolean = ???
  def toRawScala: String = s"def ${name}(${params.mkString(", ")}) = { ${nodes.map(f=>f.toRawScala).mkString(" ")} }"
  val name = "f1"
  val params: Seq[String] = Seq("a: Int", "b: Int")
  val nodes: Seq[Node] = Seq(AddOperator())
}