package nodes

class NodeTree(val rootNode: Node) extends Node {
  def isTerminal: Boolean = rootNode.isTerminal
  def canTerminate(stepsRemaining: Integer): Boolean = rootNode.canTerminate(stepsRemaining)
  def toRawScala: String = rootNode.toRawScala
  def validate: Boolean = rootNode.validate
}