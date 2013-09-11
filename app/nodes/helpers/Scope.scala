package nodes.helpers


case class Scope(numVals: Int = 0,
                 numFuncs: Int = 0,
                 numObjects: Int = 0,
                 depth: Int = 0,
                 maxExpressionsInFunc: Int = 0,
                 maxFuncsInObject: Int = 0,
                 maxParamsInFunc: Int = 0,
                 maxDepth: Int = 0,
                 maxObjectsInTree: Int = 0) {
  def incrementVals: Scope = Scope(numVals = numVals + 1,
    numFuncs = numFuncs,
    numObjects = numObjects,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth,
    maxObjectsInTree = maxObjectsInTree)

  def incrementFuncs: Scope = Scope(numVals = numVals,
    numFuncs = numFuncs + 1,
    numObjects = numObjects,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth,
    maxObjectsInTree = maxObjectsInTree)

  def incrementObjects: Scope = Scope(numVals = numVals,
    numFuncs = numFuncs,
    numObjects = numObjects + 1,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth,
    maxObjectsInTree = maxObjectsInTree)

  def incrementDepth: Scope = Scope(numVals = numVals,
    numFuncs = numFuncs,
    numObjects = numObjects,
    depth = depth + 1,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth,
    maxObjectsInTree = maxObjectsInTree)

  def setNumFuncs(newValue: Int): Scope = Scope(numVals = numVals,
    numFuncs = newValue,
    numObjects = numObjects,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth,
    maxObjectsInTree = maxObjectsInTree)

  def setNumVals(newValue: Int): Scope = Scope(numVals = newValue,
    numFuncs = numFuncs,
    numObjects = numObjects,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth,
    maxObjectsInTree = maxObjectsInTree)

  def hasDepthRemaining = depth < maxDepth
}