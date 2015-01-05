package memoization

import com.google.inject.Inject
import models.common.IScope
import utils.PozInt

import scala.concurrent.Future

class GeneratorImpl @Inject()(lookupChildren: LookupChildren) extends Generator {

  def generate(maxScope: IScope): Future[Boolean] = {
    lookupChildren.fetch(maxScope, new PozInt(1))
    Future.successful(true)
  }
}
