package replaceEmpty

import ai.RandomNumberGenerator
import composition.{StubIScope, StubRng, TestComposition}
import models.common.{IScope, Scope}
import models.domain.scala.ObjectDef
import org.mockito.Matchers._
import org.mockito.Mockito._

final class ObjectDefFactorySpec extends TestComposition {

  "create" must {
    "returns instance of this type" in {
      val (factory, scope) = objectDefFactory()

      val result = factory.create(scope = scope)

      whenReady(result) { result =>
        result mustBe an[ObjectDef]
      }
    }

    "returns expected given scope with 0 existing objects" in {
      val s = Scope(numObjects = 0, height = 10, maxFuncsInObject = 3, maxExpressionsInFunc = 3, maxParamsInFunc = 3, maxObjectsInTree = 3)
      val (factory, _) = objectDefFactory()

      val result = factory.create(scope = s)

      whenReady(result) {
        case ObjectDef(_, name) => name must equal("o0")
        case _ => fail("wrong type")
      }
    }

    "returns expected given scope with 1 existing objects" in {
      val s = Scope(numObjects = 1, height = 10, maxFuncsInObject = 3, maxExpressionsInFunc = 3, maxParamsInFunc = 3, maxObjectsInTree = 3)
      val (factory, _) = objectDefFactory()

      val result = factory.create(scope = s)

      whenReady(result) {
        case ObjectDef(_, name) => name must equal("o1")
        case _ => fail("wrong type")
      }
    }

    "returns 3 children given scope with 3 maxExpressionsInFunc (and rng mocked)" in {
      val s = Scope(numFuncs = 0, height = 10, maxFuncsInObject = 3, maxExpressionsInFunc = 3, maxParamsInFunc = 3, maxObjectsInTree = 3)
      val (factory, _) = objectDefFactory(nextInt = 3)

      val result = factory.create(scope = s)

      whenReady(result) {
        case ObjectDef(child, _) => child.length must equal(3)
        case _ => fail("wrong type")
      }
    }
  }

  "updateScope" must {
    "call increment objects" in {
      val s = mock[IScope]
      val (factory, _) = objectDefFactory()

      factory.updateScope(s)

      verify(s, times(1)).incrementObjects
    }
  }

  private def objectDefFactory(nextInt: Int = 0) = {
    val rng: RandomNumberGenerator = mock[RandomNumberGenerator]
    when(rng.nextInt(any[Int])).thenReturn(nextInt)
    val ioc = testInjector(new StubRng(rng = rng), new StubIScope)
    (ioc.getInstance(classOf[ObjectDefFactoryImpl]), ioc.getInstance(classOf[IScope]))
  }
}