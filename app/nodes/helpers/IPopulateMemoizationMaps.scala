package nodes.helpers

trait IPopulateMemoizationMaps {

  def memoizeCanTerminateInStepsRemaining(that: ICreateChildNodes,
                                          scope: IScope)

  def memoizeCanTerminateInStepsRemaining(that: ICreateChildNodes,
                                          maxExpressionsInFunc: Int,
                                          maxFuncsInObject: Int,
                                          maxParamsInFunc: Int,
                                          height: Int,
                                          maxObjectsInTree: Int)

  def run(
           maxExpressionsInFunc: Int,
           maxFuncsInObject: Int,
           maxParamsInFunc: Int,
           height: Int,
           maxObjectsInTree: Int)
}
