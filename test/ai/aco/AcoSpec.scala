package ai.aco

import ai.{RandomNumberGenerator, SelectionStrategy}
import composition.ai.aco.AcoModule
import composition.{StubRng, TestComposition}
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.{Empty, FunctionM, IntegerM, TypeTree, Object, ValDclInFunctionParam}
import org.mockito.Mockito.{times, verify}
import replaceEmpty.ReplaceEmpty

final class AcoSpec extends TestComposition {

  "chooseChild" must {
    "returns expected instance given only one valid choice" in {
      val randomNumberGenerator = mock[RandomNumberGenerator]
      val selectionStrategy = testInjector(new AcoModule, new StubRng(randomNumberGenerator)).getInstance(classOf[SelectionStrategy])
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
        maxObjectsInTree = 1)

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
      val injector = testInjector(new AcoModule, new StubRng(randomNumberGenerator))
      val selectionStrategy = injector.getInstance(classOf[SelectionStrategy])

      selectionStrategy.chooseIndex(expected)

      verify(randomNumberGenerator, times(1)).nextInt(expected)
    }
  }

  private def injector = testInjector(new AcoModule)

  private def selectionStrategy = injector.getInstance(classOf[SelectionStrategy])
}