package decision

import models.common.IScope
import models.domain.Step
import utils.PozInt

import scala.concurrent.Future

trait ObjectFactory extends Decision {

  def createNodes(scope: IScope, acc: Seq[Step] = Seq.empty): Future[AccumulateInstructions]
}

object ObjectFactory {

  val id = PozInt(5)
}
