package nodes.helpers

import com.google.inject.Inject

class PopulateMemoizationMaps @Inject()() extends IPopulateMemoizationMaps {
  def memoizeCanTerminateInStepsRemaining(map: IMemoizeDi[IScope, Boolean],
                                          that: ICreateChildNodes): Unit = {

    //val scope = Scope() // TODO use loops within loops to build up

  }

  def memoizeCanTerminateInStepsRemaining(map: IMemoizeDi[IScope, Boolean],
                                          that: ICreateChildNodes,
                                          scope: IScope): Unit = {
    map.store getOrElseUpdate(scope, that.canTerminateInStepsRemaining(scope))
  }
}
