package replaceEmpty

import models.common.IScope
import models.domain.Instruction
import scala.concurrent.duration.{SECONDS, FiniteDuration}

trait ReplaceEmpty {

  protected val finiteTimeout = FiniteDuration(1, SECONDS)

  val neighbourIds: Seq[Int] // TODO should the collection type be Array?

  def create(scope: IScope): Instruction

  def updateScope(scope: IScope): IScope
}