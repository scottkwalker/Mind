package nodes.helpers

import com.google.inject.Inject
import nodes._

class PopulateMemoizationMaps @Inject()(addOperatorFactory: AddOperatorFactory,
                                        functionMFactory: FunctionMFactory,
                                        integerMFactory: IntegerMFactory,
                                        nodeTreeFactory: NodeTreeFactory,
                                        objectDefFactory: ObjectDefFactory,
                                        valDclInFunctionParamFactory: ValDclInFunctionParamFactory,
                                        valueRefFactory: ValueRefFactory) extends IPopulateMemoizationMaps {
  def memoizeCanTerminateInStepsRemaining(that: ICreateChildNodes,
                                          scope: IScope): Unit = {
    that.mapOfCanTerminateInStepsRemaining.store getOrElseUpdate(scope, that.canTerminateInStepsRemaining(scope))
  }

  def memoizeCanTerminateInStepsRemaining(that: ICreateChildNodes,
                                          maxExpressionsInFunc: Int,
                                          maxFuncsInObject: Int,
                                          maxParamsInFunc: Int,
                                          maxDepth: Int,
                                          maxObjectsInTree: Int): Unit = {

    for (maxDepth <- 0 to maxDepth;
         maxExpressionsInFunc <- 0 to maxExpressionsInFunc;
         maxFuncsInObject <- 0 to maxFuncsInObject;
         maxParamsInFunc <- 0 to maxParamsInFunc;
         maxObjectsInTree <- 0 to maxObjectsInTree) {
      val scope = Scope(maxExpressionsInFunc = maxExpressionsInFunc,
        maxFuncsInObject = maxFuncsInObject,
        maxParamsInFunc = maxParamsInFunc,
        maxDepth = maxDepth,
        maxObjectsInTree = maxObjectsInTree)
      memoizeCanTerminateInStepsRemaining(that, scope)
    }
  }

  def run(maxExpressionsInFunc: Int,
          maxFuncsInObject: Int,
          maxParamsInFunc: Int,
          maxDepth: Int,
          maxObjectsInTree: Int): Unit = {
    val factories: Seq[ICreateChildNodes] = Seq(
      addOperatorFactory,
      functionMFactory,
      integerMFactory,
      nodeTreeFactory,
      objectDefFactory,
      valDclInFunctionParamFactory,
      valueRefFactory
    )

    for (factory <- factories) {
      memoizeCanTerminateInStepsRemaining(
        factory,
        maxExpressionsInFunc, maxFuncsInObject, maxParamsInFunc, maxDepth, maxObjectsInTree)
      // TODO Save map to file
    }
  }
}
