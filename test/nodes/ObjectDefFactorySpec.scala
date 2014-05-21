package nodes

import com.google.inject.{Injector, Guice}
import nodes.helpers.{IScope, Scope}
import ai.IRandomNumberGenerator
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule
import models.domain.scala.ObjectDef
import org.mockito.Mockito._
import org.mockito.Matchers._
import utils.helpers.UnitSpec

class ObjectDefFactorySpec extends UnitSpec {
  "create" should {
    "returns instance of this type" in {
      val s = Scope(maxDepth = 10)

      val instance = factory.create(scope = s)

      instance shouldBe an[ObjectDef]
    }

    "returns expected given scope with 0 functions" in {
      val s = Scope(numObjects = 0, maxDepth = 10)

      val instance = factory.create(scope = s)

      instance match {
        case ObjectDef(_, name) => name should equal("o0")
        case _ => fail("wrong type")
      }
    }

    "returns expected given scope with 1 functions" in {
      val s = Scope(numObjects = 1, maxDepth = 10)

      val instance = factory.create(scope = s)

      instance match {
        case ObjectDef(_, name) => name should equal("o1")
        case _ => fail("wrong type")
      }
    }

    "returns 3 children given scope with 3 maxExpressionsInFunc (and rng mocked)" in {
      val s = Scope(numFuncs = 0, maxDepth = 10, maxFuncsInObject = 3)

      val instance = factory.create(scope = s)

      instance match {
        case ObjectDef(child, _) => child.length should equal(3)
        case _ => fail("wrong type")
      }
    }
  }

  "updateScope" should {
    "call increment objects" in {
      val s = mock[IScope]

      factory.updateScope(s)

      verify(s, times(1)).incrementObjects
    }
  }

  private val rng = mock[IRandomNumberGenerator]
  when(rng.nextInt(any[Int])).thenReturn(2)
  when(rng.nextBoolean()).thenReturn(true)

  private val injector: Injector = Guice.createInjector(new DevModule(randomNumberGenerator = rng), new LegalGamerModule)
  private val factory = injector.getInstance(classOf[ObjectDefFactory])
}