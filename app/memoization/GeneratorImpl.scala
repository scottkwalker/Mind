package memoization

import com.google.inject.Inject
import models.common.IScope
import utils.PozInt

import scala.concurrent.Future

class GeneratorImpl @Inject()(lookupChildren: LookupChildren) extends Generator {

  def generate(maxScope: IScope): Future[Boolean] = {
    for(id <- lookupChildren.factoryLookup.factories) {
      lookupChildren.fetch(maxScope, id)
    }
    Future.successful(true)
  }
}
