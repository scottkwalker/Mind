package decision

import models.common.IScope
import utils.PozInt

import scala.concurrent.Future

trait FunctionMFactory extends Decision

object FunctionMFactory {

  val id = PozInt(2)
}