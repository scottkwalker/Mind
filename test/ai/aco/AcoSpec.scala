package ai.aco

import org.specs2.mutable._
import nodes.helpers._
import org.specs2.mock.Mockito
import nodes._
import com.google.inject.Guice
import fitness.AddTwoInts
import nodes.ObjectDef
import nodes.helpers.Scope
import nodes.IntegerM
import nodes.ValDclInFunctionParam
import nodes.NodeTree
import nodes.FunctionM
import ai.helpers.TestAiModule
import scala.util.Random
import ai.randomWalk.RandomWalkModule

class AcoSpec extends Specification with Mockito {
  "Aco" should {
    "chooseChild returns expected instance given only one valid choice" in {
      val rng = mock[Random]
      val sut = Aco(rng)
      val v = mock[CreateChildNodes]
      val possibleChildren = Seq(v)

      sut.chooseChild(possibleChildren) must beAnInstanceOf[CreateChildNodes]
    }

    "create and test an individual does not throw" in {
      // TODO move to new spec file
      "using test Ai that always returns index zero" in {
        val injector = Guice.createInjector(new DevModule, new TestAiModule)
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
      }.pendingUntilFixed("Error thrown after upgrading specs2")

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
      }.pendingUntilFixed("Error thrown after upgrading specs2")
    }
  }
}