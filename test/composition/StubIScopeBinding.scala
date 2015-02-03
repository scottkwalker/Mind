package composition

import com.google.inject.AbstractModule
import models.common.{IScope, Scope}
import org.scalatest.mock.MockitoSugar

final class StubIScopeBinding(
                               numFuncs: Int = 0,
                               numObjects: Int = 0
                               ) extends AbstractModule with MockitoSugar {

  val stub = {
    val scope: IScope = Scope(
      numFuncs = numFuncs,
      numObjects = numObjects,
      height = 10,
      maxExpressionsInFunc = 2,
      maxFuncsInObject = 3,
      maxParamsInFunc = 2,
      maxObjectsInTree = 3,
      maxHeight = 10)
    scope
  }

  override def configure(): Unit = bind(classOf[IScope]).toInstance(stub)
}