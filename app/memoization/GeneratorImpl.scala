package memoization

import com.google.inject.Inject
import models.common.{IScope, Scope}

import scala.concurrent.Future

class GeneratorImpl @Inject()(lookupChildren: LookupChildren) extends Generator {

  def generate(maxScope: IScope): Future[Boolean] = {
    for {
      height <- 0 to maxScope.maxHeight // Must be first val in the loop so that all lookups for one height are done in one go.
      numFuncs <- (0 to maxScope.maxFuncsInObject).par // TODO check that doing this in parallel is not drowning us with threads. Also see about execution contexts https://www.playframework.com/documentation/2.3.6/ThreadPools
      numVals <- (0 to maxScope.maxParamsInFunc).par
      numObjects <- (0 to maxScope.maxObjectsInTree).par
      currentScope: IScope = Scope(
        numVals = numVals,
        numFuncs = numFuncs,
        numObjects = numObjects,
        height = height,
        maxFuncsInObject = maxScope.maxFuncsInObject,
        maxParamsInFunc = maxScope.maxParamsInFunc,
        maxObjectsInTree = maxScope.maxObjectsInTree,
        maxHeight = maxScope.maxHeight
      )
      id <- lookupChildren.factoryLookup.factories.par
    } {
      // We can evaluate across all the types of factories in parallel as they all depend on results calculated
      // further "down" the map, no dependencies on the same level.
      lookupChildren.fetch(currentScope, id)
    }

    Future.successful(true)
  }
}
