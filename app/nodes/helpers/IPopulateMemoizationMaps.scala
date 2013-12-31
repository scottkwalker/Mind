package nodes.helpers

trait IPopulateMemoizationMaps {
  def memoizeCanTerminateInStepsRemaining(memoizeCanTerminateInStepsRemaining: IMemoizeDi[IScope, Boolean]): Unit
}
