package nodes

import com.google.inject.{Injector, Guice}
import nodes.helpers.{IScope, Scope}
import ai.IRandomNumberGenerator
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule
import models.domain.scala.FunctionM
import utils.helpers.UnitSpec
import org.mockito.Mockito._
import org.mockito.Matchers._

class FunctionMFactorySpec extends UnitSpec {
  val rng = mock[IRandomNumberGenerator]
  when(rng.nextInt(any[Int])).thenReturn(2)
  when(rng.nextBoolean()).thenReturn(true)

  val injector: Injector = Guice.createInjector(new DevModule(randomNumberGenerator = rng), new LegalGamerModule)
  val factory = injector.getInstance(classOf[FunctionMFactory])

  "create" should {
    "return instance of this type" in {
      val s = Scope(maxDepth = 10)

      val instance = factory.create(scope = s)

      instance shouldBe a[FunctionM]
    }

    "return expected given scope with 0 functions" in {
      val s = Scope(numFuncs = 0, maxDepth = 10)

      val instance = factory.create(scope = s)

      instance match {
        case FunctionM(_, _, name) => name should equal("f0")
        case _ => fail("wrong type")
      }
    }

    "return expected given scope with 1 functions" in {
      val s = Scope(numFuncs = 1, maxDepth = 10)

      val instance = factory.create(scope = s)

      instance match {
        case FunctionM(_, _, name) => name should equal( "f1")
        case _ => fail("wrong type")
      }
    }

    "returns 3 children given scope with 3 maxExpressionsInFunc (and rng mocked)" in {
      val s = Scope(numFuncs = 0, maxDepth = 10, maxParamsInFunc = 3, maxExpressionsInFunc = 3)

      val instance = factory.create(scope = s)

      instance match {
        case FunctionM(_, children, _) => children.length should equal(3)
        case _ => fail("wrong type")
      }
    }
  }

  "updateScope" should {
    "call increment functions" in {
      val s = mock[IScope]

      factory.updateScope(s)

      verify(s, times(1)).incrementFuncs
    }
  }
}