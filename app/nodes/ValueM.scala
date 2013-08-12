package nodes

case class ValueM(val name: String) extends Node {
  def toRawScala: String = name
  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else !name.isEmpty
}

case object ValueM extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq()

  override def couldTerminate(stepsRemaining: Integer) = {
    if (stepsRemaining == 0) false else true
  }
  
  def create: Node = ValueM("testCreateChild") // TODO generate a name unused so far in the scope.
}