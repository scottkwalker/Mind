package memoization

import com.google.inject.Inject
import models.common.{IScope, Scope}
import models.domain.scala.FactoryLookup
import utils.PozInt

import scala.concurrent.Future

class GeneratorImpl @Inject()(
                               factoryLookup: FactoryLookup,
                               repository: Memoize2WithSet[IScope, PozInt]
                               ) extends Generator {

  // Calculate all values from the minimum Scope up to the maxScope. Add those that can terminate to the repository.
  override def calculateAndUpdate(maxScope: IScope): Future[Int] = {
    for {
      height <- 1 to maxScope.maxHeight // Must be first val in the loop so that all calculations for one height are done in one go.
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
      id <- factoryLookup.factories.par
    } {
      // We can evaluate across all the types of factories in parallel as they all depend on results calculated
      // further "down" the map, no dependencies on the same level.
      calculateAndUpdate(currentScope, id)
    }

    Future.successful(repository.size)
  }

  override def calculateAndUpdate(scope: IScope, parent: PozInt): Boolean = {
    if (scope.hasHeightRemaining) {
      val possibleNeighbourIds = factoryLookup.convert(parent).nodesToChooseFrom
      if (possibleNeighbourIds.isEmpty) false
      else {
        val hasChildThatTerminates = possibleNeighbourIds.exists { possNeighbourId =>
          repository.apply(key1 = scope, key2 = possNeighbourId)
        }
        // If this parent at this scope has children in the repository (they are in the repository only because they can
        // terminate!) then the parent can terminate.
        if (hasChildThatTerminates) {
          repository.add(key1 = scope, key2 = parent)
          true
        }
        else false
      }
    }
    else false
  }
}
