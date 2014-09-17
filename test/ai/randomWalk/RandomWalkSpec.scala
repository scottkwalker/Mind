package ai.randomWalk

import ai.{RandomNumberGenerator, SelectionStrategy}
import com.google.inject.{AbstractModule, Injector}

import composition.{StubRng, TestComposition}
import factory.ReplaceEmpty
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.{FunctionM, IntegerM, NodeTree, ObjectDef, _}
import modules.ai.randomWalk.RandomWalkModule
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}

final class RandomWalkSpec extends TestComposition {

  "chooseChild" must {
    "return expected type given only one valid choice" in {
      val rng = mock[RandomNumberGenerator]
      val sut = RandomGamer(rng)
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
        for (i <- 1 to 10) {
          val nodeTree: NodeTree = premade.replaceEmpty(scope)(injector).asInstanceOf[NodeTree]
          val f = new AddTwoInts(nodeTree)
          f.fitness
        }
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

    "call random number generator nextInt" in {
      val expected = 2
      val rng = mock[RandomNumberGenerator]
      val injector = testInjector(new RandomWalkModule, new StubRng(rng))
      val sut = injector.getInstance(classOf[SelectionStrategy])

      sut.chooseIndex(expected)

      verify(rng, times(1)).nextInt(expected)
    }
  }

  override lazy val injector: Injector = testInjector(new RandomWalkModule)
}