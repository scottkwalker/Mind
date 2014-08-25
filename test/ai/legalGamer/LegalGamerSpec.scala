package ai.legalGamer

import ai.{IRandomNumberGenerator, SelectionStrategy}
import composition.TestComposition
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.{Empty, FunctionM, IntegerM, NodeTree, ObjectDef, ValDclInFunctionParam}
import nodes.helpers.ICreateChildNodes

final class LegalGamerSpec extends TestComposition {

  "chooseChild" must {
    "return expected type given only one valid choice" in {
      val rng = mock[IRandomNumberGenerator]
      val sut = LegalGamer(rng)
      val v = mock[ICreateChildNodes]
      val possibleChildren = Seq(v)

      sut.chooseChild(possibleChildren) mustBe a[ICreateChildNodes]
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
}