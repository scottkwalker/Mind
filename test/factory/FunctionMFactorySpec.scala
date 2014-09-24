package factory

import composition.{StubRng, TestComposition}
import models.common.{IScope, Scope}
import models.domain.scala.FunctionM
import org.mockito.Mockito._

final class FunctionMFactorySpec extends TestComposition {

  "create" must {
    "return instance of this type" in {
      val s = Scope(height = 10)

      val instance = factory.create(scope = s)

      instance mustBe a[FunctionM]
    }

    "return expected given scope with 0 functions" in {
      val s = Scope(numFuncs = 0, height = 10)

      val instance = factory.create(scope = s)

      instance match {
        case FunctionM(_, _, name) => name must equal("f0")
        case _ => fail("wrong type")
      }
    }

    "return expected given scope with 1 functions" in {
      val s = Scope(numFuncs = 1, height = 10)

      val instance = factory.create(scope = s)

      instance match {
        case FunctionM(_, _, name) => name must equal("f1")
        case _ => fail("wrong type")
      }
    }

    "returns 3 children given scope with 3 maxExpressionsInFunc (and rng mocked)" in {
      val s = Scope(numFuncs = 0, height = 10, maxParamsInFunc = 3, maxExpressionsInFunc = 3)

      val instance = factory.create(scope = s)

      instance match {
        case FunctionM(_, children, _) => children.length must equal(3)
        case _ => fail("wrong type")
      }
    }
  }

  "updateScope" must {
    "call increment functions" in {
      val s = mock[IScope]

      factory.updateScope(s)

      verify(s, times(1)).incrementFuncs
    }
  }

  private val factory = testInjector(new StubRng).getInstance(classOf[FunctionMFactoryImpl])
}