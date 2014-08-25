package ai.aco

import ai.{IRandomNumberGenerator, SelectionStrategy}
import com.google.inject.Injector
import com.tzavellas.sse.guice.ScalaModule
import composition.TestComposition
import fitness.AddTwoInts
import models.common.Scope
import models.domain.scala.{Empty, FunctionM, IntegerM, NodeTree, ObjectDef, ValDclInFunctionParam}
import modules.ai.aco.AcoModule
import nodes.helpers._
import org.mockito.Matchers._
import org.mockito.Mockito._

final class AcoSpec extends TestComposition {

  "chooseChild" must {
    "returns expected instance given only one valid choice" in {
      val rng = mock[IRandomNumberGenerator]
      val sut = Aco(rng)
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
      val rng = mock[IRandomNumberGenerator]

      final class StubRng extends ScalaModule {

        def configure(): Unit = {
          when(rng.nextInt(any[Int])).thenReturn(2)
          bind(classOf[IRandomNumberGenerator]).toInstance(rng)
        }
      }
      val injector = testInjector(new AcoModule, new StubRng)
      val sut = injector.getInstance(classOf[SelectionStrategy])

      sut.chooseIndex(expected)

      verify(rng, times(1)).nextInt(expected)
    }
  }

  override lazy val injector: Injector = testInjector(new AcoModule)
}