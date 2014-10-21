package composition

import _root_.ai.{RandomNumberGenerator, RandomNumberGeneratorImpl}
import com.google.inject.AbstractModule
import memoization._
import models.common.{IScope, Scope}
import models.domain.scala.Empty
import replaceEmpty._

final class DevModule() extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[Empty]).asEagerSingleton()
    bind(classOf[NodeTreeFactory]).to(classOf[NodeTreeFactoryImpl]).asEagerSingleton()
    bind(classOf[ObjectDefFactory]).to(classOf[ObjectDefFactoryImpl]).asEagerSingleton()
    bind(classOf[ValueRefFactory]).to(classOf[ValueRefFactoryImpl]).asEagerSingleton()
    bind(classOf[IScope]).toInstance(Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1))
    bind(classOf[CreateNode]).to(classOf[CreateNodeImpl]).asEagerSingleton()
    bind(classOf[CreateSeqNodes]).to(classOf[CreateSeqNodesImpl]).asEagerSingleton()
    bind(classOf[RandomNumberGenerator]).to(classOf[RandomNumberGeneratorImpl]).asEagerSingleton()
    bind(classOf[FactoryLookup]).to(classOf[FactoryLookupImpl]).asEagerSingleton()
    bind(classOf[LookupNeighbours]).to(classOf[LookupNeighboursImpl]).asEagerSingleton()
    bind(classOf[FunctionMFactory]).to(classOf[FunctionMFactoryImpl]).asEagerSingleton()
    bind(classOf[AddOperatorFactory]).to(classOf[AddOperatorFactoryImpl]).asEagerSingleton()
    bind(classOf[IntegerMFactory]).to(classOf[IntegerMFactoryImpl]).asEagerSingleton()
    bind(classOf[ValDclInFunctionParamFactory]).to(classOf[ValDclInFunctionParamFactoryImpl]).asEagerSingleton()
    bind(classOf[PopulateMemoizationMaps]).to(classOf[PopulateMemoizationMapsImpl]).asEagerSingleton()
  }
}