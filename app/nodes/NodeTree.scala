package nodes

class NodeTree(val rootNode: Node) extends Node {
  def canTerminate(stepsRemaining: Integer): Boolean = rootNode.canTerminate(stepsRemaining)
  def toRawScala: String = rootNode.toRawScala
  def hasNoEmptyNodes: Boolean = rootNode.hasNoEmptyNodes
}