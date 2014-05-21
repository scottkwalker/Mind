package fitness

import fitness.Fitness.maxFitness
import models.domain.scala._
import models.domain.scala.AddOperator
import models.domain.scala.ObjectDef
import models.domain.scala.FunctionM
import models.domain.scala.IntegerM
import utils.helpers.UnitSpec
import org.mockito.Mockito._

final class AddTwoIntsSpec extends UnitSpec {
  "Addition" should {
    "1 add 1 equals 2 with NodeTree that returns hard coded raw Scala" in {
      val nodeTree = mock[NodeTree]
      when(nodeTree.toRaw).thenReturn("object o0 { def f0(a: Int, b: Int) = a + b }")
      val f = new AddTwoInts(nodeTree)
      f.fitness should equal(maxFitness)
    }

    "1 add 1 equals 2 with NodeTree that converts nodes to raw Scala" in {
      val nodeTree = new NodeTree(
        Seq(
          ObjectDef(Seq(
            FunctionM(
              params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM())),
              nodes = Seq(
                AddOperator(ValueRef("a"), ValueRef("b"))
              ), name = "f0")),
            name = "o0")))
      val f = new AddTwoInts(nodeTree)
      f.fitness should equal(maxFitness)
    }

    "return score less than max fitness for a non-optimal solution" in {
      // Arrange
      val nodeTree = new NodeTree(List(ObjectDef(List(FunctionM(List(ValDclInFunctionParam("v0", IntegerM()), ValDclInFunctionParam("v1", IntegerM())), List(ValueRef("v0")), "f0")), "o0")))

      // Act
      val f = new AddTwoInts(nodeTree)

      // Assert
      f.fitness should not equal maxFitness
    }
  }
}