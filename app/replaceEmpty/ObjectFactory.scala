package replaceEmpty

import models.common.IScope
import models.domain.Instruction
import utils.PozInt

import scala.concurrent.Future

trait ObjectFactory extends ReplaceEmpty {

  def createNodes(scope: IScope, acc: Seq[Instruction] = Seq.empty): Future[AccumulateInstructions]
}

object ObjectFactory {

  val id = PozInt(5)
}
