package nodes.helpers


case class Scope(numVals: Integer = 0,
                 numFuncs: Integer = 0,
                 numObjects: Integer = 0,
                 depth: Integer = 0,
                 maxExpressionsInFunc: Integer = 0,
                 maxFuncsInObject: Integer = 0,
                 maxParamsInFunc: Integer = 0,
                 maxDepth: Integer = 0) {
  def incrementVals: Scope = Scope(numVals = numVals + 1,
    numFuncs = numFuncs,
    numObjects = numObjects,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth)

  def incrementFuncs: Scope = Scope(numVals = numVals,
    numFuncs = numFuncs + 1,
    numObjects = numObjects,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth)

  def incrementObjects: Scope = Scope(numVals = numVals,
    numFuncs = numFuncs,
    numObjects = numObjects + 1,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth)

  def incrementDepth: Scope = Scope(numVals = numVals,
    numFuncs = numFuncs,
    numObjects = numObjects,
    depth = depth + 1,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth)

  def setNumFuncs(newValue: Int): Scope = Scope(numVals = numVals,
    numFuncs = newValue,
    numObjects = numObjects,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth)

  def setNumVals(newValue: Int): Scope = Scope(numVals = newValue,
    numFuncs = numFuncs,
    numObjects = numObjects,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth)

  def hasDepthRemaining = depth < maxDepth
}