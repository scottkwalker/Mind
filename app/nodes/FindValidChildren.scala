package nodes

trait FindValidChildren {
  val allPossibleChildren: Seq[FindValidChildren]
  def validChildren(stepsRemaining: Integer) = allPossibleChildren.filter(n => n.canTerm(stepsRemaining - 1))
  def canTerm(stepsRemaining: Integer): Boolean = {
    if (stepsRemaining == 0) false else {
      allPossibleChildren.exists(n => n.canTerm(stepsRemaining - 1))
    }
  }
}