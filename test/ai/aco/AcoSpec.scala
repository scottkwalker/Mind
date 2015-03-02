package ai.aco

import ai.SelectionStrategy
import composition.StubFactoryLookupBinding
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
import models.domain.scala.FunctionMImpl$
import models.domain.scala.IntegerM
import models.domain.scala.ObjectImpl
import models.domain.scala.ObjectImpl$
import models.domain.scala.TypeTree
import models.domain.scala.ValDclInFunctionParam
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

final class AcoSpec extends UnitTestHelpers with TestComposition {

  "chooseChild" must {
    "returns expected instance given only one valid choice" in {
      val node = mock[Decision]
      val possibleChildren = Set(node)

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
        new AcoBinding,
        randomNumberGenerator
      )
      val selectionStrategy = injector.getInstance(classOf[SelectionStrategy])

      selectionStrategy.chooseIndex(expected)

      verify(randomNumberGenerator.stub, times(1)).nextInt(expected)
    }
  }

  private def factoryLookup = acoInjector.getInstance(classOf[FactoryLookup])

  private def selectionStrategy = acoInjector.getInstance(classOf[SelectionStrategy])

  private def acoInjector = testInjector(
    new AcoBinding,
    new StubFactoryLookupBinding,
    new StubRngBinding
  )
}