package decision

import models.common.IScope
import models.domain.Step
import utils.PozInt

import scala.concurrent.Future

trait TypeTreeFactory extends Decision {

  def create(scope: IScope, premadeChildren: Seq[Decision]): Future[Step]
}

object TypeTreeFactory {

  val id = PozInt(4)
}