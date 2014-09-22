package composition

import com.google.inject.AbstractModule
import models.common.{IScope, Scope}

final class StubIScope(
                        scope: IScope = Scope(height = 10,
                          maxExpressionsInFunc = 2,
                          maxFuncsInObject = 3,
                          maxParamsInFunc = 2,
                          maxObjectsInTree = 3)
                        ) extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[IScope]).toInstance(scope)
  }
}