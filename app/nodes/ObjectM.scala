package nodes

case class ObjectM(val nodes: Seq[Node]) extends Node {
  def isTerminal: Boolean = false
  def canTerminate(stepsRemaining: Integer): Boolean = ???
  def toRawScala: String = s"object ${name} ${nodes.map(f=>f.toRawScala).mkString("{ ", " ", " }")}"
  val name = "Individual"
  def validate = nodes.forall(f => f.validate)
}