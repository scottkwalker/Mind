package models.common

import play.api.data.Forms.{mapping, _}
import play.api.libs.json.Json

final case class Scope(numVals: Int = 0,
                       numFuncs: Int = 0,
                       numObjects: Int = 0,
                       height: Int = 0,
                       maxExpressionsInFunc: Int = 0,
                       maxFuncsInObject: Int = 0,
                       maxParamsInFunc: Int = 0,
                       maxObjectsInTree: Int = 0) extends IScope {

  def incrementVals: IScope = copy(numVals = numVals + 1)

  def incrementFuncs: IScope = copy(numFuncs = numFuncs + 1)

  def incrementObjects: IScope = copy(numObjects = numObjects + 1)

  def decrementHeight: IScope = copy(height = height - 1)

  def setNumFuncs(newValue: Int): IScope = copy(numFuncs = newValue)

  def setNumVals(newValue: Int): IScope = copy(numVals = newValue)

  def hasHeightRemaining: Boolean = height > 0
}

object Scope {

  implicit val jsonFormat = Json.format[Scope]

  object Form {

    // TODO May need to move to a view
    val numValsId = "numVals"
    val numFuncsId = "numFuncs"
    val numObjectsId = "numObjects"
    val heightId = "height"
    val maxExpressionsInFuncId = "maxExpressionsInFunc"
    val maxFuncsInObjectId = "maxFuncsInObject"
    val maxParamsInFuncId = "maxParamsInFunc"
    val maxObjectsInTreeId = "maxObjectsInTree"

    final val Mapping = mapping(
      numValsId -> number,
      numFuncsId -> number,
      numObjectsId -> number,
      heightId -> number,
      maxExpressionsInFuncId -> number,
      maxFuncsInObjectId -> number,
      maxParamsInFuncId -> number,
      maxObjectsInTreeId -> number
    )(Scope.apply)(Scope.unapply)
  }

}
