package nodes.helpers

import com.google.inject.Inject
import nodes._

/* Plan
We have a distinct array of factories allFactories
We have a mutable Map of type [Scope, Future[Seq[Int]]].
The Int represents a factory number.
Each Future is a lambda of def populate(scope) = Future { allThoseThatCanTerminate(scope) } ).
A parallel loop starts from the lowest numbers and adds all scope -> populate(scope) to the Map. Call Map(scope).map to
make it start calculating an answer.
Json serializer runs over the Map
*/

final class PopulateMemoizationMaps @Inject()(addOperatorFactory: AddOperatorFactory,
                                        functionMFactory: FunctionMFactory,
                                        integerMFactory: IntegerMFactory,
                                        nodeTreeFactory: NodeTreeFactory,
                                        objectDefFactory: ObjectDefFactory,
                                        valDclInFunctionParamFactory: ValDclInFunctionParamFactory,
                                        valueRefFactory: ValueRefFactory) extends IPopulateMemoizationMaps {
  def memoizeCanTerminateInStepsRemaining(that: ICreateChildNodes,
                                          scope: IScope): Unit = {
    //that.mapOfCanTerminateInStepsRemaining getOrElseUpdate(scope, that.canTerminateInStepsRemaining(scope))
  }

  def memoizeCanTerminateInStepsRemaining(that: ICreateChildNodes,
                                          maxExpressionsInFunc: Int,
                                          maxFuncsInObject: Int,
                                          maxParamsInFunc: Int,
                                          maxDepth: Int,
                                          maxObjectsInTree: Int): Unit = {

    for (depth <- 0 to maxDepth;
         expressionsInFunc <- 0 to maxExpressionsInFunc;
         funcsInObject <- 0 to maxFuncsInObject;
         paramsInFunc <- 0 to maxParamsInFunc;
         objectsInTree <- 0 to maxObjectsInTree) {
      val scope = Scope(maxExpressionsInFunc = expressionsInFunc,
        maxFuncsInObject = funcsInObject,
        maxParamsInFunc = paramsInFunc,
        maxDepth = depth,
        maxObjectsInTree = objectsInTree)
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