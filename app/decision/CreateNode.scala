package decision

import models.common.IScope
import models.domain.Step

import scala.concurrent.Future

trait CreateNode {

  def create(possibleChildren: Future[Set[Decision]],
             scope: IScope): Future[(IScope, Step)]
}
