package nodes

case class Empty() extends Node {
  def isTerminal = true
}

case object Empty{
}