package nodes

case class Value(val name: String) extends Node {
  def canTerminate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else true
  def toRawScala: String = name
  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else !name.isEmpty
}

case object Value