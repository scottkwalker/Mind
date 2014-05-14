package nodes.helpers

import play.api.libs.json.{Format, Writes, Json}

case class Scope(numVals: Int = 0,
                 numFuncs: Int = 0,
                 numObjects: Int = 0,
                 depth: Int = 0,
                 maxExpressionsInFunc: Int = 0,
                 maxFuncsInObject: Int = 0,
                 maxParamsInFunc: Int = 0,
                 maxDepth: Int = 0,
                 maxObjectsInTree: Int = 0) extends IScope {
  def incrementVals: IScope = Scope(numVals = numVals + 1,
    numFuncs = numFuncs,
    numObjects = numObjects,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth,
    maxObjectsInTree = maxObjectsInTree)

  def incrementFuncs: IScope = Scope(numVals = numVals,
    numFuncs = numFuncs + 1,
    numObjects = numObjects,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth,
    maxObjectsInTree = maxObjectsInTree)

  def incrementObjects: IScope = Scope(numVals = numVals,
    numFuncs = numFuncs,
    numObjects = numObjects + 1,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth,
    maxObjectsInTree = maxObjectsInTree)

  def incrementDepth: IScope = Scope(numVals = numVals,
    numFuncs = numFuncs,
    numObjects = numObjects,
    depth = depth + 1,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth,
    maxObjectsInTree = maxObjectsInTree)

  def setNumFuncs(newValue: Int): IScope = Scope(numVals = numVals,
    numFuncs = newValue,
    numObjects = numObjects,
    depth = depth,
    maxExpressionsInFunc = maxExpressionsInFunc,
    maxFuncsInObject = maxFuncsInObject,
    maxParamsInFunc = maxParamsInFunc,
    maxDepth = maxDepth,
    maxObjectsInTree = maxObjectsInTree)

  def setNumVals(newValue: Int): IScope = Scope(numVals = newValue,
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

object Scope {
  implicit val jsonFormat = Json.format[Scope]
}


object Serialiser {
  def toJson[A: Writes](model: A) = {
    Json.toJson(model)
  }
}