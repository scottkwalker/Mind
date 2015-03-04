package ai.aco

import ai.SelectionStrategy
import composition.StubFactoryLookupAnyBinding
import composition.StubRngBinding
import composition.TestComposition
import composition.UnitTestHelpers
import composition.ai.aco.AcoBinding
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
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

final class AcoSpec extends UnitTestHelpers with TestComposition {

  "chooseChild" must {
    "returns expected instance given only one valid choice" in {
      val node = mock[Decision]
      val possibleChildren = Set(node)
      val (selectionStrategy, _, _) = build

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
      val (_, factoryLookup, _) = build

      try {
        for (i <- 1 to 10) {
          val result = premade.fillEmptySteps(scope, factoryLookup)
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
      val (selectionStrategy, _, _) = build
      a[RuntimeException] must be thrownBy selectionStrategy.chooseChild(possibleChildren = Set.empty[Decision])
    }
  }

  "chooseIndex" must {
    "throw when length is zero" in {
      val (selectionStrategy, _, _) = build
      a[RuntimeException] must be thrownBy selectionStrategy.chooseIndex(seqLength = 0)
    }

    "call random number generator nextInt" in {
      val expected = 2
      val (selectionStrategy, _, randomNumberGenerator) = build

      selectionStrategy.chooseIndex(expected)

      verify(randomNumberGenerator, times(1)).nextInt(expected)
      verifyNoMoreInteractions(randomNumberGenerator)
    }
  }

  private def build = {
    val randomNumberGenerator = new StubRngBinding
    val injector = testInjector(
      new AcoBinding,
      new StubFactoryLookupAnyBinding,
      randomNumberGenerator
    )
    (injector.getInstance(classOf[SelectionStrategy]), injector.getInstance(classOf[FactoryLookup]), randomNumberGenerator.stub)
  }
}