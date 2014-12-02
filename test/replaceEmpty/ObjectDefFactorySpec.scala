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
      val (objectDefFactory, scope) = build()

      val result = objectDefFactory.create(scope = scope)

      whenReady(result) { result =>
        result mustBe an[ObjectDef]
      }
    }

    "returns expected given scope with 0 existing objects" in {
      val (objectDefFactory, scope) = build(numObjects = 0)

      val result = objectDefFactory.create(scope = scope)

      whenReady(result) {
        case ObjectDef(_, name) => name must equal("o0")
        case _ => fail("wrong type")
      }
    }

    "returns expected given scope with 1 existing objects" in {
      val (objectDefFactory, scope) = build(numObjects = 1)

      val result = objectDefFactory.create(scope = scope)

      whenReady(result) {
        case ObjectDef(_, name) => name must equal("o1")
        case _ => fail("wrong type")
      }
    }

    "returns 3 children given scope with 3 maxExpressionsInFunc (and rng mocked)" in {
      val (objectDefFactory, scope) = build(nextInt = 3)

      val result = objectDefFactory.create(scope = scope)

      whenReady(result) {
        case ObjectDef(child, _) => child.length must equal(3)
        case _ => fail("wrong type")
      }
    }
  }

  "updateScope" must {
    "call increment objects" in {
      val scope = mock[IScope]
      val (objectDefFactory, _) = build()

      objectDefFactory.updateScope(scope)

      verify(scope, times(1)).incrementObjects
    }
  }

  private def build(nextInt: Int = 0, numObjects: Int = 1) = {
    val rng: RandomNumberGenerator = mock[RandomNumberGenerator]
    when(rng.nextInt(any[Int])).thenReturn(nextInt)
    val injector = testInjector(new StubRng(randomNumberGenerator = rng), new StubIScope(numObjects = numObjects))
    (injector.getInstance(classOf[ObjectDefFactoryImpl]), injector.getInstance(classOf[IScope]))
  }
}