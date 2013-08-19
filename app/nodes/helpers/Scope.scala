package nodes.helpers

case class Scope(val numVals: Integer = 0,
  val numFuncs: Integer = 0,
  val numObjects: Integer = 0,
  val stepsRemaining: Integer = 0) {
  def incrementVals: Scope = Scope(numVals = numVals + 1, numFuncs, numObjects, stepsRemaining)
  def incrementFuncs: Scope = Scope(numVals, numFuncs = numFuncs + 1, numObjects, stepsRemaining)
  def incrementObjects: Scope = Scope(numVals, numFuncs, numObjects = numObjects + 1, stepsRemaining)
  def decrementStepsRemaining: Scope = Scope(numVals, numFuncs, numObjects, stepsRemaining = stepsRemaining - 1)
  val noStepsRemaining = stepsRemaining == 0
}