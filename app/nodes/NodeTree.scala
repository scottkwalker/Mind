package nodes

class NodeTree(val rootNode: Node) extends Node {
  def canTerminate(stepsRemaining: Integer): Boolean = rootNode.canTerminate(stepsRemaining)
  def toRawScala: String = rootNode.toRawScala
  
  def validate: Boolean = rootNode match {
    case ObjectM(n) => rootNode.validate
    case Empty() => false
    case _ => false
  }
}