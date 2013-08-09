package nodes

case class ObjectM(val nodes: Seq[Node]) extends Node {
  def canTerminate(stepsRemaining: Integer): Boolean = nodes.forall(f => f.canTerminate(stepsRemaining - 1))
  def toRawScala: String = s"object ${name} ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  val name = "Individual"

  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else nodes.forall(n => n match {
    case _: Method => n.validate(stepsRemaining - 1)
    case _: Empty => false
    case _ => false
  })
}