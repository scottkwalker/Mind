package modules

import _root_.ai.{RandomNumberGenerator, IRandomNumberGenerator}
import com.tzavellas.sse.guice.ScalaModule
import nodes.helpers._
import nodes.helpers.CreateNode
import nodes.FunctionMFactory
import nodes.ObjectDefFactory
import nodes.AddOperatorFactory
import nodes.ValDclInFunctionParamFactory
import nodes.helpers.Scope
import nodes.ValueRefFactory
import nodes.helpers.CreateSeqNodes
import nodes.NodeTreeFactory
import models.domain.scala.Empty
import nodes.legalNeighbours.{FactoryIdToFactoryImpl, FactoryIdToFactory, LegalNeighboursImpl, LegalNeighbours}

class DevModule(scope: IScope = Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1),
                randomNumberGenerator: IRandomNumberGenerator = RandomNumberGenerator()) extends ScalaModule {
  def configure(): Unit = {
    bindAddOperatorFactory()
    bind(classOf[Empty]).asEagerSingleton()
    bindFunctionMFactory()
    bind(classOf[NodeTreeFactory]).asEagerSingleton()
    bind(classOf[ObjectDefFactory]).asEagerSingleton()
    bind(classOf[ValueRefFactory]).asEagerSingleton()
    bindValDclInFunctionParamFactory()
    bind(classOf[IScope]).toInstance(scope)
    //bind(classOf[ICreateNode]).toInstance(createNode)
    bind(classOf[ICreateNode]).to(classOf[CreateNode]).asEagerSingleton()
    bind(classOf[ICreateSeqNodes]).to(classOf[CreateSeqNodes])
    bind(classOf[IRandomNumberGenerator]).toInstance(randomNumberGenerator)
    bindIPopulateMemoizationMaps()
    bind(classOf[FactoryIdToFactory]).to(classOf[FactoryIdToFactoryImpl]).asEagerSingleton()
    bind(classOf[LegalNeighbours]).to(classOf[LegalNeighboursImpl]).asEagerSingleton()
  }

  def bindFunctionMFactory(): Unit = {
    bind(classOf[FunctionMFactory]).asEagerSingleton()
  }

  def bindAddOperatorFactory(): Unit = {
    bind(classOf[AddOperatorFactory]).asEagerSingleton()
  }

  def bindValDclInFunctionParamFactory(): Unit = {
    bind(classOf[ValDclInFunctionParamFactory]).asEagerSingleton()
  }

  def bindIPopulateMemoizationMaps(): Unit = {
    bind(classOf[IPopulateMemoizationMaps]).to(classOf[PopulateMemoizationMaps]).asEagerSingleton()
  }
}