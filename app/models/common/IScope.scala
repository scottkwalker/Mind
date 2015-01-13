package models.common

import play.api.libs.json._

trait IScopeWithMax {

  val maxHeight: Int
  val maxExpressionsInFunc: Int
  val maxFuncsInObject: Int
  val maxParamsInFunc: Int
  val maxObjectsInTree: Int
}

trait IScopeWithCurrent {

  val numVals: Int
  val numFuncs: Int
  val numObjects: Int
  val height: Int
}

trait IScope extends IScopeWithMax with IScopeWithCurrent {

  def incrementVals: IScope

  def incrementFuncs: IScope

  def incrementObjects: IScope

  def decrementHeight: IScope

  def setNumFuncs(newValue: Int): IScope

  def setNumVals(newValue: Int): IScope

  def hasHeightRemaining: Boolean
}

object IScope {

  implicit val jsonReads: Reads[IScope] = __.read[Scope](Json.reads[Scope]).map(x => x: Scope)

  implicit val jsonWrites = Writes[IScope] {
    case s: Scope => models.common.Scope.jsonFormat.writes(s)
  }
}