package memoization

import com.google.inject.Inject
import memoization.RepositoryWithSetImpl.writes
import models.common.IScope
import models.domain.scala.FactoryLookup
import play.api.libs.json._
import utils.PozInt

import scala.language.implicitConversions

class RepositoryWithSetImpl @Inject() (factoryLookup: FactoryLookup)
    extends Memoize2WithSetImpl[IScope, PozInt](factoryLookup.version)(writes) {
}

object RepositoryWithSetImpl {

  private[memoization] implicit val writes = new Writes[Set[String]] {
    def writes(cache: Set[String]): JsValue = Json.toJson(cache)
  }

  private[memoization] implicit def reads(factoryLookup: FactoryLookup): Reads[RepositoryWithSetImpl] =
    (__ \ "versioning").read[String].flatMap[RepositoryWithSetImpl] {
      case versioningFromFile =>
        require(versioningFromFile == factoryLookup.version, s"version info from file ($versioningFromFile) did not match the intended versioning (${factoryLookup.version})")
        (__ \ "cache").read[Set[String]].map {
          setOfKeys =>
            val repo = new RepositoryWithSetImpl(factoryLookup)
            repo.add(setOfKeys)
            repo
        }
    }
}