package ai.aco

import org.specs2.mutable._
import nodes.helpers._
import org.specs2.mock.Mockito
import com.google.inject.Guice
import fitness.AddTwoInts
import ai.IRandomNumberGenerator
import modules.ai.aco.AcoModule
import modules.DevModule
import models.domain.scala._
import nodes.helpers.Scope
import models.domain.scala.IntegerM
import models.domain.scala.ObjectDef
import models.domain.scala.NodeTree
import models.domain.scala.FunctionM

class AcoSpec extends Specification with Mockito {
  "Aco" should {
    "chooseChild returns expected instance given only one valid choice" in {
      val rng = mock[IRandomNumberGenerator]
      val sut = Aco(rng)
      val v = mock[ICreateChildNodes]
      val possibleChildren = Seq(v)

      sut.chooseChild(possibleChildren) must beAnInstanceOf[ICreateChildNodes]
    }

    "create and test an individual does not throw" in {
      "using ACO module" in {
        val injector = Guice.createInjector(new DevModule, new AcoModule)
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
          for (i <- 1 to 10) {
            val nodeTree: NodeTree = premade.replaceEmpty(scope, injector).asInstanceOf[NodeTree]

            //println(nodeTree)
            //println("validate: " + nodeTree.validate(scope))
            //println("toRawScala: " + nodeTree.toRawScala)
            val f = new AddTwoInts(nodeTree)
            f.fitness
          }
          success
        }
        catch {
          case e: Throwable => failure("Should not have thrown exception: " + e + ", stacktrace: " + e.getStackTrace)
        }
      }
    }
  }
}