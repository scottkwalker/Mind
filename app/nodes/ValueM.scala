package nodes

case class ValueM(val name: String) extends Node {
  def canTerminate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else true
  def toRawScala: String = name
  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else !name.isEmpty
}

case object ValueM extends FindValidChildren {
  val allPossibleChildren = Seq()

  override def canTerm(stepsRemaining: Integer) = {
    if (stepsRemaining == 0) false else true
  }
}