package nodes

class NodeTree(val rootNode: Node) {
  def toRawScala: String = rootNode.toRawScala
  def validate: Boolean = rootNode.validate
}