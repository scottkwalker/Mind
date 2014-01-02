package nodes.helpers

import com.google.inject.Inject

class PopulateMemoizationMaps @Inject()() extends IPopulateMemoizationMaps {
  def memoizeCanTerminateInStepsRemaining(map: IMemoizeDi[IScope, Boolean],
                                          that: ICreateChildNodes,
                                          numVals: Int,
                                          numFuncs: Int,
                                          numObjects: Int,
                                          maxExpressionsInFunc: Int,
                                          maxFuncsInObject: Int,
                                          maxParamsInFunc: Int,
                                          maxDepth: Int,
                                          maxObjectsInTree: Int): Unit = {


    for (maxDepth <- 0 to maxDepth;
         numVals <- 0 to numVals;
         numFuncs <- 0 to numFuncs;
         numObjects <- 0 to numObjects;
         maxExpressionsInFunc <- 0 to maxExpressionsInFunc;
         maxFuncsInObject <- 0 to maxFuncsInObject;
         maxParamsInFunc <- 0 to maxParamsInFunc;
         maxObjectsInTree <- 0 to maxObjectsInTree) {
      val scope = Scope(numVals, numFuncs, numObjects, maxExpressionsInFunc, maxFuncsInObject, maxParamsInFunc, maxDepth, maxObjectsInTree)
      memoizeCanTerminateInStepsRemaining(map, that, scope)
    }

  }

  def memoizeCanTerminateInStepsRemaining(map: IMemoizeDi[IScope, Boolean],
                                          that: ICreateChildNodes,
                                          scope: IScope): Unit = {
    map.store getOrElseUpdate(scope, that.canTerminateInStepsRemaining(scope))
  }
}
