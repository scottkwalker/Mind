package decision

import models.common.IScope
import models.domain.Step
import utils.PozInt

import scala.concurrent.Future

trait ObjectFactory extends Decision

object ObjectFactory {

  val id = PozInt(5)
}
