package ai.legalGamer

import ai.SelectionStrategy
import composition.TestHelpers
import composition.TestComposition
import composition.ai.legalGamer.LegalGamerBinding
import decision.Decision
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.Empty
import models.domain.scala.FactoryLookup
import models.domain.scala.FunctionM
import models.domain.scala.IntegerM
import models.domain.scala.Object
import models.domain.scala.TypeTree
import models.domain.scala.ValDclInFunctionParam

final class LegalGamerSpec extends TestHelpers with TestComposition {

  "chooseChild" must {
    "return expected type given only one valid choice" in {
      val selectionStrategy = testInjector().getInstance(classOf[SelectionStrategy])
      val node = mock[Decision]
      val possibleChildren = Set(node)

      selectionStrategy.chooseChild(possibleChildren) mustBe a[Decision]
    }

    "return code that can be compiled and evaluated" in {
      val premade = new TypeTree(
        Seq(
          Object(Seq(
            FunctionM(
              params = Seq(ValDclInFunctionParam("v0", IntegerM()), ValDclInFunctionParam("v1", IntegerM())),
              nodes = Seq(
                Empty()
              ), name = "f0")),
            name = "o0")))
      val scope = Scope(
        maxExpressionsInFunc = 1,
        maxFuncsInObject = 1,
        maxParamsInFunc = 2,
        height = 5,
        maxObjectsInTree = 1,
        maxHeight = 10)

      try {
        val result = premade.fillEmptySteps(scope, factoryLookup)
        whenReady(result) {
          case typeTree: TypeTree =>
            val f = new AddTwoInts(typeTree)
            f.fitness
        }(config = patienceConfig)
      }
      catch {
        case e: Throwable => fail("must not have thrown exception: " + e + ", stacktrace: " + e.getStackTrace)
      }
    }

    "throw when sequence is empty" in {
      a[RuntimeException] must be thrownBy selectionStrategy.chooseChild(possibleChildren = Set.empty[Decision])
    }
  }

  "chooseIndex" must {
    "throw when length is zero" in {
      a[RuntimeException] must be thrownBy selectionStrategy.chooseIndex(seqLength = 0)
    }

    "always returns zero" in {
      selectionStrategy.chooseIndex(2) must equal(0)
    }
  }

  "canAddAnother" must {
    "return false when accumulator length equals factoryLimit" in {
      selectionStrategy.canAddAnother(accLength = 1, factoryLimit = 1) must equal(false)
    }

    "return false when accumulator length is greater than factoryLimit" in {
      selectionStrategy.canAddAnother(accLength = 2, factoryLimit = 1) must equal(false)
    }

    "return true when accumulator length is less than factoryLimit" in {
      selectionStrategy.canAddAnother(accLength = 1, factoryLimit = 2) must equal(true)
    }
  }

  private def factoryLookup = legalGamerInjector.getInstance(classOf[FactoryLookup])

  private def selectionStrategy = legalGamerInjector.getInstance(classOf[SelectionStrategy])

  private def legalGamerInjector = testInjector(new LegalGamerBinding)
}