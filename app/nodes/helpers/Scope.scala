package nodes.helpers


case class Scope(numVals: Integer = 0,
                 accumulatorLength: Integer = 0,
                 numFuncs: Integer = 0,
                 numObjects: Integer = 0,
                 stepsRemaining: Integer = 0,
                 maxExpressionsInFunc: Integer = 0,
                 maxFuncsInObject: Integer = 0,
                 maxParamsInFunc: Integer = 0) {
  def incrementVals: Scope = Scope(numVals = numVals + 1,
    accumulatorLength = accumulatorLength,
    numFuncs = numFuncs,
    numObjects = numObjects,
    stepsRemaining = stepsRemaining,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject)

  def incrementAccumulatorLength: Scope = Scope(numVals = numVals,
    accumulatorLength = accumulatorLength + 1,
    numFuncs = numFuncs,
    numObjects = numObjects,
    stepsRemaining = stepsRemaining,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject)

  def incrementFuncs: Scope = Scope(numVals = numVals,
    accumulatorLength = accumulatorLength,
    numFuncs = numFuncs + 1,
    numObjects = numObjects,
    stepsRemaining = stepsRemaining,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject)

  def incrementObjects: Scope = Scope(numVals = numVals,
    accumulatorLength = accumulatorLength,
    numFuncs = numFuncs,
    numObjects = numObjects + 1,
    stepsRemaining = stepsRemaining,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject)

  def decrementStepsRemaining: Scope = Scope(numVals = numVals,
    accumulatorLength = accumulatorLength,
    numFuncs = numFuncs,
    numObjects = numObjects,
    stepsRemaining = stepsRemaining - 1,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject)

  val noStepsRemaining = stepsRemaining == 0

  def paramsHasSpaceForChildren = accumulatorLength < maxParamsInFunc

  def funcHasSpaceForChildren = accumulatorLength < maxExpressionsInFunc

  def objHasSpaceForChildren = accumulatorLength < maxFuncsInObject

  def resetAccumulator = Scope(numVals = numVals,
    accumulatorLength = 0,
    numFuncs = numFuncs,
    numObjects = numObjects,
    stepsRemaining = stepsRemaining,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject)

  def copyAccumulatorToNumVals = Scope(numVals = accumulatorLength,
    accumulatorLength = accumulatorLength,
    numFuncs = numFuncs,
    numObjects = numObjects,
    stepsRemaining = stepsRemaining,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject)
}