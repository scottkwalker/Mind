package ai.randomWalk

import ai.{IRandomNumberGenerator, SelectionStrategy}
import com.google.inject.Injector
import com.tzavellas.sse.guice.ScalaModule
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.{FunctionM, IntegerM, NodeTree, ObjectDef, _}
import modules.ai.randomWalk.RandomWalkModule
import nodes.helpers._
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import utils.helpers.UnitSpec

final class RandomWalkSpec extends UnitSpec {

  "chooseChild" should {
    "return expected type given only one valid choice" in {
      val rng = mock[IRandomNumberGenerator]
      val sut = RandomGamer(rng)
      val v = mock[ICreateChildNodes]
      val possibleChildren = Seq(v)

      sut.chooseChild(possibleChildren) shouldBe a[ICreateChildNodes]
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
        case e: Throwable => fail("Should not have thrown exception: " + e + ", stacktrace: " + e.getStackTrace)
      }
    }

    "throw when sequence is empty" in {
      val sut = injector.getInstance(classOf[SelectionStrategy])
      a[RuntimeException] should be thrownBy sut.chooseChild(possibleChildren = Seq.empty)
    }
  }

  "chooseIndex" should {
    "throw when length is zero" in {
      val sut = injector.getInstance(classOf[SelectionStrategy])
      a[RuntimeException] should be thrownBy sut.chooseIndex(seqLength = 0)
    }

    "call random number generator nextInt" in {
      val expected = 2
      val rng = mock[IRandomNumberGenerator]

      final class StubRng extends ScalaModule {

        def configure(): Unit = {
          when(rng.nextInt(any[Int])).thenReturn(2)
          bind(classOf[IRandomNumberGenerator]).toInstance(rng)
        }
      }
      val injector = testInjector(new RandomWalkModule, new StubRng)
      val sut = injector.getInstance(classOf[SelectionStrategy])

      sut.chooseIndex(expected)

      verify(rng, times(1)).nextInt(expected)
    }
  }

  override lazy val injector: Injector = testInjector(new RandomWalkModule)
}