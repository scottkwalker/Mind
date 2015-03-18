package ai.legalGamer

import ai.SelectionStrategy
import composition.StubFactoryLookupAnyBinding
import composition.StubRngBinding
import composition.TestComposition
import composition.UnitTestHelpers
import composition.ai.legalGamer.LegalGamerBinding
import decision.Decision
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.Empty
import models.domain.scala.FactoryLookup
import models.domain.scala.FunctionMImpl
import models.domain.scala.IntegerM
import models.domain.scala.ObjectImpl
import models.domain.scala.TypeTree
import models.domain.scala.ValDclInFunctionParam

final class LegalGamerSpec extends UnitTestHelpers with TestComposition {

  "chooseChild" must {
    "return an instance of the expected type" in {
      val node = mock[Decision]
      val possibleChildren = Set(node)
      val (selectionStrategy, _) = build

      selectionStrategy.chooseChild(possibleChildren) mustBe a[Decision]
    }

    "return code that can be compiled and evaluated" in {
      val premade = new TypeTree(
        Seq(
          ObjectImpl(Seq(
            FunctionMImpl(
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
      val (_, factoryLookup) = build

      try {
        val typeTreeF = premade.fillEmptySteps(scope, factoryLookup)
        whenReady(typeTreeF) { typeTree =>
          val f = new AddTwoInts(typeTree)
          f.fitness
        }(config = patienceConfig)
      }
      catch {
        case e: Throwable => fail("must not have thrown exception: " + e + ", stacktrace: " + e.getStackTrace)
      }
    }

    "throw if sequence is empty" in {
      val (selectionStrategy, _) = build
      a[RuntimeException] must be thrownBy selectionStrategy.chooseChild(possibleChildren = Set.empty[Decision])
    }
  }

  "chooseIndex" must {
    "throw if length is zero" in {
      val (selectionStrategy, _) = build
      a[RuntimeException] must be thrownBy selectionStrategy.chooseIndex(seqLength = 0)
    }

    "always returns zero" in {
      val (selectionStrategy, _) = build
      selectionStrategy.chooseIndex(seqLength = 2) must equal(0)
    }
  }

  "canAddAnother" must {
    "return false if the accumulator length is equal to the factoryLimit" in {
      val (selectionStrategy, _) = build
      selectionStrategy.canAddAnother(accLength = 1, factoryLimit = 1) must equal(false)
    }

    "return false if the accumulator length is greater than factoryLimit" in {
      val (selectionStrategy, _) = build
      selectionStrategy.canAddAnother(accLength = 2, factoryLimit = 1) must equal(false)
    }

    "return true if the accumulator length is less than factoryLimit" in {
      val (selectionStrategy, _) = build
      selectionStrategy.canAddAnother(accLength = 1, factoryLimit = 2) must equal(true)
    }
  }

  private def build = {
    val injector = testInjector(
      new LegalGamerBinding,
      new StubFactoryLookupAnyBinding,
      new StubRngBinding
    )
    (injector.getInstance(classOf[SelectionStrategy]), injector.getInstance(classOf[FactoryLookup]))
  }
}