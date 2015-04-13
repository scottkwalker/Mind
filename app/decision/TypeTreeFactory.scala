package decision

import models.common.IScope
import models.domain.Step
import utils.PozInt

import scala.concurrent.Future

trait TypeTreeFactory extends Decision {

  // This is an extra fuction for the top level of a tree that is passed some pre-made children.
  def fillEmptySteps(scope: IScope, premadeChildren: Seq[Decision]): Future[Step]
}

object TypeTreeFactory {

  val id = PozInt(4)
}