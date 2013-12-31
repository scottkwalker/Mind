package nodes.helpers

trait IPopulateMemoizationMaps {
  def memoizeCanTerminateInStepsRemaining(map: IMemoizeDi[IScope, Boolean],
                                          that: ICreateChildNodes,
                                          numVals: Int,
                                          numFuncs: Int,
                                          numObjects: Int)

  def memoizeCanTerminateInStepsRemaining(map: IMemoizeDi[IScope, Boolean],
                                          that: ICreateChildNodes,
                                          scope: IScope): Unit
}
