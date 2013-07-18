package nodes

case class ObjectM() extends Node {
  def isTerminal: Boolean = false
  def canTerminate(stepsRemaining: Integer): Boolean = ???
  def toRawScala: String = s"object ${name} { ${nodes.map(f=>f.toRawScala).mkString(" ")} }"
  val name = "Individual"
  val nodes: Seq[Node] = Seq(Method())
}