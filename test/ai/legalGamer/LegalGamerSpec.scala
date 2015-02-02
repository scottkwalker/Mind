package ai.legalGamer

import ai.{RandomNumberGenerator, SelectionStrategy}
import composition.{StubRng, TestComposition}
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.{Empty, FunctionM, IntegerM, Object, TypeTree, ValDclInFunctionParam}
import replaceEmpty.ReplaceEmpty

final class LegalGamerSpec extends TestComposition {

  "chooseChild" must {
    "return expected type given only one valid choice" in {
      val randomNumberGenerator = mock[RandomNumberGenerator]
      val selectionStrategy = testInjector(new StubRng(randomNumberGenerator)).getInstance(classOf[SelectionStrategy])
      val node = mock[ReplaceEmpty]
      val possibleChildren = Set(node)

      selectionStrategy.chooseChild(possibleChildren) mustBe a[ReplaceEmpty]
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
        val result = premade.replaceEmpty(scope)(injector)
        whenReady(result) {
          case typeTree: TypeTree =>
            val f = new AddTwoInts(typeTree)
            f.fitness
        }(config = whenReadyPatienceConfig)
      }
      catch {
        case e: Throwable => fail("must not have thrown exception: " + e + ", stacktrace: " + e.getStackTrace)
      }
    }

    "throw when sequence is empty" in {
      a[RuntimeException] must be thrownBy selectionStrategy.chooseChild(possibleChildren = Set.empty[ReplaceEmpty])
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

  private def injector = testInjector()

  private def selectionStrategy = injector.getInstance(classOf[SelectionStrategy])
}