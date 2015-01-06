package memoization

import com.google.inject.Inject
import models.common.{IScope, Scope}

import scala.concurrent.Future

class GeneratorImpl @Inject()(lookupChildren: LookupChildren) extends Generator {

  def generate(maxScope: IScope): Future[Boolean] = {
    for {
      _ <- 0 to maxScope.maxExpressionsInFunc // THIS IS NOT USED SO SHOULD BE REMOVED WITH OTHERS FROM THE MAPPING AND HTML
      numFuncs <- 0 to maxScope.maxFuncsInObject
      numVals <- 0 to maxScope.maxParamsInFunc
      numObjects <- 0 to maxScope.maxObjectsInTree
      height <- 0 to maxScope.height
    } yield {
      val currentScope = Scope(
        numVals = numVals,
        numFuncs = numFuncs,
        numObjects = numObjects,
        height = height
      )
      // We can evaluate across all the types of factories in parallel as they all depend on results calculated
      // further "down" the map, no dependencies on the same level.
      lookupChildren.factoryLookup.factories.par.map(id => lookupChildren.fetch(currentScope, id))
    }

    Future.successful(true)
  }
}
