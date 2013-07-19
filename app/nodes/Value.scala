package nodes

case class Value(val name: String) extends Node {
  def isTerminal: Boolean = true
  def canTerminate(stepsRemaining: Integer): Boolean = true
  def toRawScala: String = name
  def validate = true
}