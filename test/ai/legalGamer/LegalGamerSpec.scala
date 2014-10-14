package ai.legalGamer

import ai.{RandomNumberGenerator, SelectionStrategy}
import composition.{StubRng, TestComposition}
import replaceEmpty.ReplaceEmpty
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.{Empty, FunctionM, IntegerM, NodeTree, ObjectDef, ValDclInFunctionParam}

final class LegalGamerSpec extends TestComposition {

  "chooseChild" must {
    "return expected type given only one valid choice" in {
      val rng = mock[RandomNumberGenerator]
      val sut = testInjector(new StubRng(rng)).getInstance(classOf[SelectionStrategy])
      val v = mock[ReplaceEmpty]
      val possibleChildren = Seq(v)

      sut.chooseChild(possibleChildren) mustBe a[ReplaceEmpty]
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
        val nodeTree: NodeTree = premade.replaceEmpty(scope)(injector).asInstanceOf[NodeTree]
        val f = new AddTwoInts(nodeTree)
        f.fitness
      }
      catch {
        case e: Throwable => fail("must not have thrown exception: " + e + ", stacktrace: " + e.getStackTrace)
      }
    }

    "throw when sequence is empty" in {
      val sut = injector.getInstance(classOf[SelectionStrategy])
      a[RuntimeException] must be thrownBy sut.chooseChild(possibleChildren = Seq.empty)
    }
  }

  "chooseIndex" must {
    "throw when length is zero" in {
      val sut = injector.getInstance(classOf[SelectionStrategy])
      a[RuntimeException] must be thrownBy sut.chooseIndex(seqLength = 0)
    }

    "always returns zero" in {
      val sut = injector.getInstance(classOf[SelectionStrategy])
      sut.chooseIndex(2) must equal(0)
    }
  }

  private val injector = testInjector()
}