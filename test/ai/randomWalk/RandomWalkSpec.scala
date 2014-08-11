package ai.randomWalk

import ai.IRandomNumberGenerator
import com.google.inject.{Injector, Guice}
import fitness.AddTwoInts
import models.domain.scala.{FunctionM, IntegerM, NodeTree, ObjectDef, _}
import modules.DevModule
import modules.ai.randomWalk.RandomWalkModule
import nodes.helpers.{Scope, _}
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
  }

  override lazy val injector: Injector = testInjector(new RandomWalkModule)
}