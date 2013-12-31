package nodes.helpers

import com.google.inject.Inject

class PopulateMemoizationMaps @Inject()() extends IPopulateMemoizationMaps {
  def memoizeCanTerminateInStepsRemaining(map: IMemoizeDi[IScope, Boolean],
                                          that: ICreateChildNodes,
                                          numVals: Int,
                                          numFuncs: Int,
                                          numObjects: Int): Unit = {


    for (numVals <- 0 to numVals;
         numFuncs <- 0 to numFuncs;
         numObjects <- 0 to numObjects) {
      val scope = Scope(numVals, numFuncs, numObjects)
      memoizeCanTerminateInStepsRemaining(map, that, scope)
    }

  }

  def memoizeCanTerminateInStepsRemaining(map: IMemoizeDi[IScope, Boolean],
                                          that: ICreateChildNodes,
                                          scope: IScope): Unit = {
    map.store getOrElseUpdate(scope, that.canTerminateInStepsRemaining(scope))
  }
}
