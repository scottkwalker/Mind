package nodes

class NodeTree(val rootNode: Node) {
  def toRawScala: String = rootNode.toRawScala
}