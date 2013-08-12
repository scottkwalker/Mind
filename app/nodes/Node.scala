package nodes

trait Node {
  def toRawScala: String
  def validate(stepsRemaining: Integer): Boolean
}