package nodes

trait Node {
  def canTerminate(stepsRemaining: Integer): Boolean
  def toRawScala: String
  def hasNoEmptyNodes: Boolean
}