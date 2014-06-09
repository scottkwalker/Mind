package nodes.helpers

import play.api.libs.json._

trait IScope {
  val numVals: Int
  val numFuncs: Int
  val numObjects: Int
  val depth: Int
  val maxExpressionsInFunc: Int
  val maxFuncsInObject: Int
  val maxParamsInFunc: Int
  val maxDepth: Int
  val maxObjectsInTree: Int

  def incrementVals: IScope

  def incrementFuncs: IScope

  def incrementObjects: IScope

  def incrementDepth: IScope

  def setNumFuncs(newValue: Int): IScope

  def setNumVals(newValue: Int): IScope

  def hasDepthRemaining: Boolean
}

object IScope {
  implicit val jsonReads: Reads[IScope] = __.read[Scope](Json.reads[Scope]).map(x => x:Scope)

  implicit val jsonWrites = Writes[IScope] {
    case s: Scope => nodes.helpers.Scope.jsonFormat.writes(s)
  }
}