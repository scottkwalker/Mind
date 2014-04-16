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

class DevModule(scope: IScope = Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1),
                randomNumberGenerator: IRandomNumberGenerator = RandomNumberGenerator(),
                createNode: ICreateNode = CreateNode()) extends ScalaModule {
  def configure(): Unit = {
    bindAddOperatorFactory()
    bind(classOf[Empty]).asEagerSingleton()
    bindFunctionMFactory()
    bind(classOf[NodeTreeFactory]).asEagerSingleton()
    bind(classOf[ObjectDefFactory]).asEagerSingleton()
    bind(classOf[ValueRefFactory]).asEagerSingleton()
    bindValDclInFunctionParamFactory()
    bind(classOf[IScope]).toInstance(scope)
    bind(classOf[ICreateNode]).toInstance(createNode)
    bind(classOf[ICreateSeqNodes]).to(classOf[CreateSeqNodes])
    bind(classOf[IRandomNumberGenerator]).toInstance(randomNumberGenerator)
    bindIPopulateMemoizationMaps()
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