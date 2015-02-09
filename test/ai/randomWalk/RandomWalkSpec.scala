package ai.randomWalk

import ai.SelectionStrategy
import composition.ai.randomWalk.RandomWalkBinding
import composition.StubRngBinding
import composition.TestComposition
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.FunctionM
import models.domain.scala.IntegerM
import models.domain.scala.Object
import models.domain.scala.TypeTree
import models.domain.scala._
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import decision.Decision

final class RandomWalkSpec extends TestComposition {

  "chooseChild" must {
    "return expected type given only one valid choice" in {
      val randomNumberGenerator = new StubRngBinding
      val sut = testInjector(
        new RandomWalkBinding,
        randomNumberGenerator
      ).getInstance(classOf[SelectionStrategy])
      val node = mock[Decision]
      val possibleChildren = Set(node)

      sut.chooseChild(possibleChildren) mustBe a[Decision]
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
        for (i <- 1 to 10) {
          val result = premade.fillEmptySteps(scope)(injector)
          whenReady(result) {
            case typeTree: TypeTree =>
              val f = new AddTwoInts(typeTree)
              f.fitness
          }(config = patienceConfig)
        }
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

    "call random number generator nextInt" in {
      val expected = 2
      val randomNumberGenerator = new StubRngBinding
      val injector = testInjector(
        new RandomWalkBinding,
        randomNumberGenerator
      )
      val selectionStrategy = injector.getInstance(classOf[SelectionStrategy])

      selectionStrategy.chooseIndex(expected)

      verify(randomNumberGenerator.stub, times(1)).nextInt(expected)
    }
  }

  private def injector = testInjector(new RandomWalkBinding)

  private def selectionStrategy = injector.getInstance(classOf[SelectionStrategy])
}