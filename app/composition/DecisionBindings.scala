package composition

import com.google.inject.AbstractModule
import decision._

final class DecisionBindings extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[TypeTreeFactory]).to(classOf[TypeTreeFactoryImpl])
    bind(classOf[ObjectFactory]).to(classOf[ObjectFactoryImpl])
    bind(classOf[ValueRefFactory]).to(classOf[ValueRefFactoryImpl])
    bind(classOf[FunctionMFactory]).to(classOf[FunctionMFactoryImpl])
    bind(classOf[AddOperatorFactory]).to(classOf[AddOperatorFactoryImpl])
    bind(classOf[ValDclInFunctionParamFactory])
      .to(classOf[ValDclInFunctionParamFactoryImpl])
  }
}
