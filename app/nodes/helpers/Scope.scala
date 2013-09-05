package nodes.helpers


case class Scope(numVals: Integer = 0,
                 accumulatorLength: Integer = 0,
                 numFuncs: Integer = 0,
                 numObjects: Integer = 0,
                 stepsRemaining: Integer = 0,
                 maxExpressionsInFunc: Integer = 0,
                 maxFuncsInObject: Integer = 0) {
  def incrementVals: Scope = Scope(numVals = numVals + 1, accumulatorLength, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncsInObject)
  def incrementAccumulatorLength: Scope = Scope(numVals, accumulatorLength = accumulatorLength + 1, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncsInObject)
  def incrementFuncs: Scope = Scope(numVals, accumulatorLength, numFuncs = numFuncs + 1, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncsInObject)
  def incrementObjects: Scope = Scope(numVals, accumulatorLength, numFuncs, numObjects = numObjects + 1, stepsRemaining, maxExpressionsInFunc, maxFuncsInObject)
  def decrementStepsRemaining: Scope = Scope(numVals, accumulatorLength, numFuncs, numObjects, stepsRemaining = stepsRemaining - 1, maxExpressionsInFunc, maxFuncsInObject)
  val noStepsRemaining = stepsRemaining == 0
  def funcHasSpaceForChildren = accumulatorLength < maxExpressionsInFunc
  def objHasSpaceForChildren = accumulatorLength < maxFuncsInObject
}