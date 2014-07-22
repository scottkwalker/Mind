package ai.legalGamer

import com.google.inject.Guice
import fitness.AddTwoInts
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule
import models.domain.scala._
import nodes.helpers.{ICreateChildNodes, Scope}
import models.domain.scala.IntegerM
import models.domain.scala.ObjectDef
import models.domain.scala.NodeTree
import models.domain.scala.FunctionM
import utils.helpers.UnitSpec
import ai.IRandomNumberGenerator

final class LegalGamerSpec extends UnitSpec {
  "chooseChild" should {
    "return expected type given only one valid choice" in {
      val rng = mock[IRandomNumberGenerator]
      val sut = LegalGamer(rng)
      val v = mock[ICreateChildNodes]
      val possibleChildren = Seq(v)

      sut.chooseChild(possibleChildren) shouldBe a[ICreateChildNodes]
    }

    "return code that can be compiled and evaluated" in {
      val injector = Guice.createInjector(new DevModule, new LegalGamerModule)
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
        depth = 5,
        maxObjectsInTree = 1)

      try {
        val nodeTree: NodeTree = premade.replaceEmpty(scope, injector).asInstanceOf[NodeTree]
        val f = new AddTwoInts(nodeTree)
        f.fitness
      }
      catch {
        case e: Throwable => fail("Should not have thrown exception: " + e + ", stacktrace: " + e.getStackTrace)
      }
    }
  }
}