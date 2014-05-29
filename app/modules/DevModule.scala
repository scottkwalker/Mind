package modules

import _root_.ai.{RandomNumberGenerator, IRandomNumberGenerator}
import com.tzavellas.sse.guice.ScalaModule
import nodes.helpers._
import nodes._
import nodes.helpers.Scope
import nodes.legalNeighbours.{FactoryIdToFactoryImpl, FactoryIdToFactory, LegalNeighboursImpl, LegalNeighbours}
import nodes.helpers.CreateNode
import models.domain.scala.Empty
import nodes.helpers.CreateSeqNodes

class DevModule(scope: IScope = Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1),
                randomNumberGenerator: IRandomNumberGenerator = RandomNumberGenerator()) extends ScalaModule {
  def configure(): Unit = {
    bindAddOperatorFactory()
    bind(classOf[Empty]).asEagerSingleton()
    bindFunctionMFactory()
    bind(classOf[NodeTreeFactory]).to(classOf[NodeTreeFactoryImpl]).asEagerSingleton()
    bind(classOf[ObjectDefFactory]).to(classOf[ObjectDefFactoryImpl]).asEagerSingleton()
    bind(classOf[ValueRefFactory]).to(classOf[ValueRefFactoryImpl]).asEagerSingleton()
    bindValDclInFunctionParamFactory()
    bind(classOf[IScope]).toInstance(scope)
    bind(classOf[ICreateNode]).to(classOf[CreateNode]).asEagerSingleton()
    bind(classOf[ICreateSeqNodes]).to(classOf[CreateSeqNodes]).asEagerSingleton()
    bind(classOf[IRandomNumberGenerator]).toInstance(randomNumberGenerator)
    bindIPopulateMemoizationMaps()
    bind(classOf[FactoryIdToFactory]).to(classOf[FactoryIdToFactoryImpl]).asEagerSingleton()
    bind(classOf[LegalNeighbours]).to(classOf[LegalNeighboursImpl]).asEagerSingleton()
  }

  def bindFunctionMFactory(): Unit = {
    bind(classOf[FunctionMFactory]).to(classOf[FunctionMFactoryImpl]).asEagerSingleton()
  }

  def bindAddOperatorFactory(): Unit = {
    bind(classOf[AddOperatorFactory]).to(classOf[AddOperatorFactoryImpl]).asEagerSingleton()
  }

  def bindValDclInFunctionParamFactory(): Unit = {
    bind(classOf[ValDclInFunctionParamFactory]).to(classOf[ValDclInFunctionParamFactoryImpl]).asEagerSingleton()
  }

  def bindIPopulateMemoizationMaps(): Unit = {
    bind(classOf[IPopulateMemoizationMaps]).to(classOf[PopulateMemoizationMaps]).asEagerSingleton()
  }
}