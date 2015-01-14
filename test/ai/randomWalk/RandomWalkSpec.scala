package ai.randomWalk

import ai.{RandomNumberGenerator, SelectionStrategy}
import composition.ai.randomWalk.RandomWalkModule
import composition.{StubRng, TestComposition}
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.{FunctionM, IntegerM, TypeTree, Object, _}
import org.mockito.Mockito.{times, verify}
import replaceEmpty.ReplaceEmpty

final class RandomWalkSpec extends TestComposition {

  "chooseChild" must {
    "return expected type given only one valid choice" in {
      val randomNumberGenerator = mock[RandomNumberGenerator]
      val sut = testInjector(new RandomWalkModule, new StubRng(randomNumberGenerator)).getInstance(classOf[SelectionStrategy])
      val node = mock[ReplaceEmpty]
      val possibleChildren = Set(node)

      sut.chooseChild(possibleChildren) mustBe a[ReplaceEmpty]
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
          val result = premade.replaceEmpty(scope)(injector)
          whenReady(result) {
            case typeTree: TypeTree =>
              val f = new AddTwoInts(typeTree)
              f.fitness
          }
        }
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

    "call random number generator nextInt" in {
      val expected = 2
      val randomNumberGenerator = mock[RandomNumberGenerator]
      val injector = testInjector(new RandomWalkModule, new StubRng(randomNumberGenerator))
      val selectionStrategy = injector.getInstance(classOf[SelectionStrategy])

      selectionStrategy.chooseIndex(expected)

      verify(randomNumberGenerator, times(1)).nextInt(expected)
    }
  }

  private def injector = testInjector(new RandomWalkModule)

  private def selectionStrategy = injector.getInstance(classOf[SelectionStrategy])
}