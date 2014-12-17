package replaceEmpty

import models.common.IScope
import models.domain.Instruction
import scala.concurrent.Future

trait CreateNode {

  def create(possibleChildren: Future[Set[ReplaceEmpty]], scope: IScope): Future[(IScope, Instruction)]
}
