package memoization

import com.google.inject.Inject
import models.common.IScope

import scala.concurrent.Future

class GeneratorImpl @Inject()(lookupChildren: LookupChildren) extends Generator {

  def generate(maxScope: IScope): Future[Boolean] = {
    // We can evaluate across all the types of factories in parallel as they all depend on results calculated
    // further "down" the map, no dependencies on the same level.
    lookupChildren.factoryLookup.factories.par.map(id => lookupChildren.fetch(maxScope, id))

    Future.successful(true)
  }
}
