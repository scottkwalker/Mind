package nodes

case class Value(val name: String) extends Node {
  def canTerminate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else true
  def toRawScala: String = name
  def validate: Boolean = !name.isEmpty
}

case object Value