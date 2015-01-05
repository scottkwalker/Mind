package memoization

import com.google.inject.Inject
import models.common.Scope
import utils.PozInt

import scala.concurrent.Future

class GeneratorImpl @Inject()(lookupChildren: LookupChildren) extends Generator {

  def generate: Future[Boolean] = {
    lookupChildren.fetch(Scope(), new PozInt(1))
    Future.successful(true)
  }
}
