package modules

import _root_.ai.{IRandomNumberGenerator, RandomNumberGenerator}
import com.tzavellas.sse.guice.ScalaModule
import models.domain.scala.Empty
import nodes._
import nodes.helpers._
import nodes.legalNeighbours.{FactoryIdToFactory, FactoryIdToFactoryImpl, LegalNeighbours, LegalNeighboursImpl}

class DevModule() extends ScalaModule {

  def configure(): Unit = {
    bind(classOf[Empty]).asEagerSingleton()
    bind(classOf[NodeTreeFactory]).to(classOf[NodeTreeFactoryImpl]).asEagerSingleton()
    bind(classOf[ObjectDefFactory]).to(classOf[ObjectDefFactoryImpl]).asEagerSingleton()
    bind(classOf[ValueRefFactory]).to(classOf[ValueRefFactoryImpl]).asEagerSingleton()
    bind(classOf[IScope]).toInstance(Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1))
    bind(classOf[ICreateNode]).to(classOf[CreateNode]).asEagerSingleton()
    bind(classOf[ICreateSeqNodes]).to(classOf[CreateSeqNodes]).asEagerSingleton()
    bind(classOf[IRandomNumberGenerator]).to(classOf[RandomNumberGenerator]).asEagerSingleton()
    bind(classOf[FactoryIdToFactory]).to(classOf[FactoryIdToFactoryImpl]).asEagerSingleton()
    bind(classOf[LegalNeighbours]).to(classOf[LegalNeighboursImpl]).asEagerSingleton()
    bind(classOf[FunctionMFactory]).to(classOf[FunctionMFactoryImpl]).asEagerSingleton()
    bind(classOf[AddOperatorFactory]).to(classOf[AddOperatorFactoryImpl]).asEagerSingleton()
    bind(classOf[ValDclInFunctionParamFactory]).to(classOf[ValDclInFunctionParamFactoryImpl]).asEagerSingleton()
    bind(classOf[IPopulateMemoizationMaps]).to(classOf[PopulateMemoizationMaps]).asEagerSingleton()
  }
}