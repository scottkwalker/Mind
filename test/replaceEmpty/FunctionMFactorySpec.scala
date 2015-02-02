package replaceEmpty

import ai.RandomNumberGenerator
import composition.{StubIScope, StubRng, TestComposition}
import models.common.{IScope, Scope}
import models.domain.scala.FunctionM
import org.mockito.Matchers._
import org.mockito.Mockito._

final class FunctionMFactorySpec extends TestComposition {

  "create" must {
    "return instance of this type" in {
      val (factory, scope) = functionMFactory()

      val result = factory.create(scope = scope)

      whenReady(result) { result =>
        result mustBe a[FunctionM]
      }(config = whenReadyPatienceConfig)
    }

    "return expected given scope with 0 functions" in {
      val (factory, scope) = functionMFactory()

      val result = factory.create(scope = scope)

      whenReady(result) {
        case FunctionM(_, _, name) => name must equal("f0")
        case _ => fail("wrong type")
      }(config = whenReadyPatienceConfig)
    }

    "return expected given scope with 1 functions" in {
      val (factory, scope) = functionMFactory(numFuncs = 1)

      val result = factory.create(scope = scope)

      whenReady(result) {
        case FunctionM(_, _, name) => name must equal("f1")
        case _ => fail("wrong type")
      }(config = whenReadyPatienceConfig)
    }

    "returns 3 children given scope with 3 maxExpressionsInFunc (and rng mocked)" in {
      val (factory, scope) = functionMFactory(nextInt = 3)

      val result = factory.create(scope = scope)

      whenReady(result) {
        case FunctionM(_, children, _) => children.length must equal(3)
        case _ => fail("wrong type")
      }(config = whenReadyPatienceConfig)
    }
  }

  "updateScope" must {
    "call increment functions" in {
      val scope = mock[IScope]
      val (factory, _) = functionMFactory()

      factory.updateScope(scope = scope)

      verify(scope, times(1)).incrementFuncs
    }
  }

  private def functionMFactory(nextInt: Int = 0, numFuncs: Int = 0) = {
    val rng: RandomNumberGenerator = mock[RandomNumberGenerator]
    when(rng.nextInt(any[Int])).thenReturn(nextInt)
    val injector = testInjector(new StubRng(randomNumberGenerator = rng), new StubIScope(numFuncs = numFuncs))
    (injector.getInstance(classOf[FunctionMFactoryImpl]), injector.getInstance(classOf[IScope]))
  }
}