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

    val ScopeId = "scope"

    val NumValsId = "numVals"
    val NumValsMin = 0
    val NumValsMax = 99
    val NumValsMaxLength = 2

    val NumFuncsId = "numFuncs"
    val NumFuncsMin = 0
    val NumFuncsMax = 99
    val NumFuncsMaxLength = 2

    val NumObjectsId = "numObjects"
    val NumObjectsMin = 0
    val NumObjectsMax = 99
    val NumObjectsMaxLength = 2

    val HeightId = "height"
    val HeightMin = 0
    val HeightMax = 99
    val HeightMaxLength = 2

    val MaxExpressionsInFuncId = "maxExpressionsInFunc"
    val MaxExpressionsInFuncMin = 0
    val MaxExpressionsInFuncMax = 99
    val MaxExpressionsInFuncMaxLength = 2

    val MaxFuncsInObjectId = "maxFuncsInObject"
    val MaxFuncsInObjectMin = 0
    val MaxFuncsInObjectMax = 99
    val MaxFuncsInObjectMaxLength = 2

    val MaxParamsInFuncId = "maxParamsInFunc"
    val MaxParamsInFuncMin = 0
    val MaxParamsInFuncMax = 99
    val MaxParamsInFuncMaxLength = 2

    val MaxObjectsInTreeId = "maxObjectsInTree"
    val MaxObjectsInTreeMin = 0
    val MaxObjectsInTreeMax = 99
    val MaxObjectsInTreeMaxLength = 2

    val Mapping = mapping(
      s"$NumValsId" -> number(min = NumValsMin, max = NumValsMax),
      s"$NumFuncsId" -> number(min = NumFuncsMin, max = NumFuncsMax),
      s"$NumObjectsId" -> number(min = NumObjectsMin, max = NumObjectsMax),
      s"$HeightId" -> number(min = HeightMin, max = HeightMax),
      s"$MaxExpressionsInFuncId" -> number(min = MaxExpressionsInFuncMin, max = MaxExpressionsInFuncMax),
      s"$MaxFuncsInObjectId" -> number(min = MaxFuncsInObjectMin, max = MaxFuncsInObjectMax),
      s"$MaxParamsInFuncId" -> number(min = MaxParamsInFuncMin, max = MaxParamsInFuncMax),
      s"$MaxObjectsInTreeId" -> number(min = MaxObjectsInTreeMin, max = MaxObjectsInTreeMax)
    )(Scope.apply)(Scope.unapply)
  }

}

