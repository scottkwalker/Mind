package nodes

trait Node {
  def isTerminal: Boolean
  def canTerminate(stepsRemaining: Integer): Boolean
  def toRawScala: String
  def validate: Boolean
}