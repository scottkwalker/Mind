package composition

import com.google.inject.AbstractModule
import models.common.{IScope, Scope}

final class StubIScope(
                        numFuncs: Int = 0,
                        numObjects: Int = 0
                        ) extends AbstractModule {

  def configure(): Unit = {
    val scope: IScope = Scope(
      numFuncs = numFuncs,
      numObjects = numObjects,
      height = 10,
      maxExpressionsInFunc = 2,
      maxFuncsInObject = 3,
      maxParamsInFunc = 2,
      maxObjectsInTree = 3)
    bind(classOf[IScope]).toInstance(scope)
  }
}