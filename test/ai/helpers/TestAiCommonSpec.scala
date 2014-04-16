package ai.helpers

import org.specs2.mutable._
import org.specs2.mock.Mockito
import com.google.inject.Guice
import fitness.AddTwoInts
import modules.ai.legalGamer.LegalGamerModule
import modules.DevModule
import models.domain.scala._
import nodes.helpers.Scope
import models.domain.scala.IntegerM
import models.domain.scala.ObjectDef
import models.domain.scala.NodeTree
import models.domain.scala.FunctionM

class TestAiCommonSpec extends Specification with Mockito {
  "TestAiCommon" should {
    "create and test an individual does not throw" in {
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
        maxDepth = 5,
        maxObjectsInTree = 1)

      try {
        val nodeTree: NodeTree = premade.replaceEmpty(scope, injector).asInstanceOf[NodeTree]
        val f = new AddTwoInts(nodeTree)
        f.fitness

        success
      }
      catch {
        case e: Throwable => failure("Should not have thrown exception: " + e + ", stacktrace: " + e.getStackTrace)
      }
    }
  }
}