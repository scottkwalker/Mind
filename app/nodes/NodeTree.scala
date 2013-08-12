package nodes

class NodeTree(val rootNode: Node) extends Node {
  def toRawScala: String = rootNode.toRawScala

  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else rootNode match {
    case _: ObjectM => rootNode.validate(stepsRemaining - 1)
    case _: Empty => false
    case _ => false
  }
}

case object NodeTree extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(ObjectM)

  def create: Node = new NodeTree(allPossibleChildren(0).create)
}