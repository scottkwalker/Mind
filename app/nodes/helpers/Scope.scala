package nodes.helpers

import play.api.libs.json.{Reads, Format, Writes, Json}
import scala.collection.immutable.BitSet
import models.domain.common.JsonValidationException

final case class Scope(numVals: Int = 0,
                 numFuncs: Int = 0,
                 numObjects: Int = 0,
                 depth: Int = 0,
                 maxExpressionsInFunc: Int = 0,
                 maxFuncsInObject: Int = 0,
                 maxParamsInFunc: Int = 0,
                 maxDepth: Int = 0,
                 maxObjectsInTree: Int = 0) extends IScope {
  def incrementVals: IScope = copy(numVals = numVals + 1)

  def incrementFuncs: IScope = copy(numFuncs = numFuncs + 1)

  def incrementObjects: IScope = copy(numObjects = numObjects + 1)

  def incrementDepth: IScope = copy(depth = depth + 1)

  def setNumFuncs(newValue: Int): IScope = copy(numFuncs = newValue)

  def setNumVals(newValue: Int): IScope = copy(numVals = newValue)

  def hasDepthRemaining: Boolean = depth < maxDepth
}

object Scope {
  implicit val jsonFormat = Json.format[Scope]
}

final class JsonSerialiser {
  def serialize[A: Writes](model: A) = {
    Json.toJson(model)
  }
  
  def deserialize[A: Reads](data: String) = {
    val parsed = Json.parse(data)
    val fromJson = Json.fromJson[A](parsed)
    fromJson.asEither match {
      case Left(errors) => throw JsonValidationException(errors)
      case Right(model) => model
    }
  }
}