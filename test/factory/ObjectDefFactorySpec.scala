package factory

import ai.IRandomNumberGenerator
import com.tzavellas.sse.guice.ScalaModule
import composition.TestComposition
import models.common.{IScope, Scope}
import models.domain.scala.ObjectDef
import org.mockito.Matchers._
import org.mockito.Mockito._

final class ObjectDefFactorySpec extends TestComposition {

  "create" must {
    "returns instance of this type" in {
      val s = Scope(height = 10)

      val instance = factory.create(scope = s)

      instance mustBe an[ObjectDef]
    }

    "returns expected given scope with 0 functions" in {
      val s = Scope(numObjects = 0, height = 10)

      val instance = factory.create(scope = s)

      instance match {
        case ObjectDef(_, name) => name must equal("o0")
        case _ => fail("wrong type")
      }
    }

    "returns expected given scope with 1 functions" in {
      val s = Scope(numObjects = 1, height = 10)

      val instance = factory.create(scope = s)

      instance match {
        case ObjectDef(_, name) => name must equal("o1")
        case _ => fail("wrong type")
      }
    }

    "returns 3 children given scope with 3 maxExpressionsInFunc (and rng mocked)" in {
      val s = Scope(numFuncs = 0, height = 10, maxFuncsInObject = 3)

      val instance = factory.create(scope = s)

      instance match {
        case ObjectDef(child, _) => child.length must equal(3)
        case _ => fail("wrong type")
      }
    }
  }

  "updateScope" must {
    "call increment objects" in {
      val s = mock[IScope]

      factory.updateScope(s)

      verify(s, times(1)).incrementObjects
    }
  }

  override lazy val injector = {
    final class StubRng extends ScalaModule {

      def configure(): Unit = {
        val rng = mock[IRandomNumberGenerator]
        when(rng.nextInt(any[Int])).thenReturn(2)
        when(rng.nextBoolean).thenReturn(true)
        bind(classOf[IRandomNumberGenerator]).toInstance(rng)
      }
    }

    testInjector(new StubRng)
  }
  private val factory = injector.getInstance(classOf[ObjectDefFactoryImpl])
}