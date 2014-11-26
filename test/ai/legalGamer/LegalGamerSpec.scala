package ai.legalGamer

import ai.{RandomNumberGenerator, SelectionStrategy}
import composition.{StubRng, TestComposition}
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.{Empty, FunctionM, IntegerM, NodeTree, ObjectDef, ValDclInFunctionParam}
import replaceEmpty.ReplaceEmpty

final class LegalGamerSpec extends TestComposition {

  "chooseChild" must {
    "return expected type given only one valid choice" in {
      val randomNumberGenerator = mock[RandomNumberGenerator]
      val selectionStrategy = testInjector(new StubRng(randomNumberGenerator)).getInstance(classOf[SelectionStrategy])
      val node = mock[ReplaceEmpty]
      val possibleChildren = Seq(node)

      selectionStrategy.chooseChild(possibleChildren) mustBe a[ReplaceEmpty]
    }

    "return code that can be compiled and evaluated" in {
      val premade = new NodeTree(
        Seq(
          ObjectDef(Seq(
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
        maxObjectsInTree = 1)

      try {
        val result = premade.replaceEmpty(scope)(injector)
        whenReady(result) {
          case nodeTree: NodeTree =>
            val f = new AddTwoInts(nodeTree)
            f.fitness
        }
      }
      catch {
        case e: Throwable => fail("must not have thrown exception: " + e + ", stacktrace: " + e.getStackTrace)
      }
    }

    "throw when sequence is empty" in {
      val selectionStrategy = injector.getInstance(classOf[SelectionStrategy])
      a[RuntimeException] must be thrownBy selectionStrategy.chooseChild(possibleChildren = Seq.empty)
    }
  }

  "chooseIndex" must {
    "throw when length is zero" in {
      val selectionStrategy = injector.getInstance(classOf[SelectionStrategy])
      a[RuntimeException] must be thrownBy selectionStrategy.chooseIndex(seqLength = 0)
    }

    "always returns zero" in {
      val selectionStrategy = injector.getInstance(classOf[SelectionStrategy])
      selectionStrategy.chooseIndex(2) must equal(0)
    }
  }

  private def injector = testInjector()
}