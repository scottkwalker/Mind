package modules

import _root_.ai.{RandomNumberGenerator, IRandomNumberGenerator}
import com.tzavellas.sse.guice.ScalaModule
import com.google.inject.TypeLiteral
import nodes.helpers._
import nodes.helpers.CreateNode
import nodes.FunctionMFactory
import nodes.ObjectDefFactory
import nodes.AddOperatorFactory
import nodes.Empty
import nodes.ValDclInFunctionParamFactory
import nodes.helpers.Scope
import nodes.ValueRefFactory
import nodes.helpers.CreateSeqNodes
import nodes.helpers.MemoizeDi
import nodes.NodeTreeFactory

class DevModule(scope: IScope = Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1),
                randomNumberGenerator: IRandomNumberGenerator = RandomNumberGenerator(),
                createNode: ICreateNode = CreateNode()) extends ScalaModule {
  def configure() {
    bindAddOperatorFactory
    bind(classOf[Empty]).asEagerSingleton()
    bind(classOf[FunctionMFactory]).asEagerSingleton()
    bind(classOf[NodeTreeFactory]).asEagerSingleton()
    bind(classOf[ObjectDefFactory]).asEagerSingleton()
    bind(classOf[ValueRefFactory]).asEagerSingleton()
    bindValDclInFunctionParamFactory
    bind(classOf[IScope]).toInstance(scope)
    bind(classOf[ICreateNode]).toInstance(createNode)
    bind(classOf[ICreateSeqNodes]).to(classOf[CreateSeqNodes])
    bind(classOf[IRandomNumberGenerator]).toInstance(randomNumberGenerator)
    bind(new TypeLiteral[IMemoizeDi[IScope, Seq[ICreateChildNodes]]]() {}).to(classOf[MemoizeDi[IScope, Seq[ICreateChildNodes]]])
    bind(new TypeLiteral[IMemoizeDi[IScope, Boolean]]() {}).to(classOf[MemoizeDi[IScope, Boolean]])
    bindIPopulateMemoizationMaps
  }

  def bindAddOperatorFactory = bind(classOf[AddOperatorFactory]).asEagerSingleton()

  def bindValDclInFunctionParamFactory = bind(classOf[ValDclInFunctionParamFactory]).asEagerSingleton()

  def bindIPopulateMemoizationMaps = bind(classOf[IPopulateMemoizationMaps]).to(classOf[PopulateMemoizationMaps]).asEagerSingleton()
}