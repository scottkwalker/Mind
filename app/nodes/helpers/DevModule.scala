package nodes.helpers

import com.tzavellas.sse.guice.ScalaModule
import nodes._
import ai.{RandomNumberGenerator, IRandomNumberGenerator}
import com.google.inject.TypeLiteral

class DevModule(scope: IScope = Scope(maxExpressionsInFunc = 2, maxFuncsInObject = 10, maxParamsInFunc = 2, maxObjectsInTree = 1),
                randomNumberGenerator: IRandomNumberGenerator = RandomNumberGenerator(),
                 createNode: ICreateNode = CreateNode()) extends ScalaModule {
  def configure() {
    bind(classOf[AddOperatorFactory]).asEagerSingleton()
    bind(classOf[Empty]).asEagerSingleton()
    bind(classOf[FunctionMFactory]).asEagerSingleton()
    bind(classOf[NodeTreeFactory]).asEagerSingleton()
    bind(classOf[ObjectDefFactory]).asEagerSingleton()
    bind(classOf[ValueRefFactory]).asEagerSingleton()
    bind(classOf[ValDclInFunctionParamFactory]).asEagerSingleton()
    bind(classOf[IScope]).toInstance(scope)
    bind(classOf[ICreateNode]).toInstance(createNode)
    bind(classOf[ICreateSeqNodes]).to(classOf[CreateSeqNodes])
    bind(classOf[IRandomNumberGenerator]).toInstance(randomNumberGenerator)
    bind(new TypeLiteral [IMemoizeDi[IScope, Seq[ICreateChildNodes]]] () {}).to(classOf[MemoizeDi[IScope, Seq[ICreateChildNodes]]])
    bind(new TypeLiteral [IMemoizeDi[IScope, Boolean]] () {}).to(classOf[MemoizeDi[IScope, Boolean]])
    bind(classOf[IPopulateMemoizationMaps]).to(classOf[PopulateMemoizationMaps]).asEagerSingleton()
  }

}