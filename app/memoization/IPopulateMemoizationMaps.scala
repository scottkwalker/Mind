package memoization

import factory.ReplaceEmpty
import models.common.IScope

trait IPopulateMemoizationMaps {

  def memoizeCanTerminateInStepsRemaining(that: ReplaceEmpty,
                                          scope: IScope)

  def memoizeCanTerminateInStepsRemaining(that: ReplaceEmpty,
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
