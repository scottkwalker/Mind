package fitness

import composition.TestHelpers
import models.domain.Step
import models.domain.scala.AddOperator
import models.domain.scala.FunctionM
import models.domain.scala.IntegerM
import models.domain.scala.Object
import models.domain.scala._
import org.mockito.Mockito._

final class AddTwoIntsSpec extends TestHelpers {

  "Addition" must {
    "1 add 1 equals 2 with TypeTree that returns hard coded raw Scala" in {
      val steps = mock[Step]
      when(steps.toCompilable).thenReturn("object o0 { def f0(a: Int, b: Int) = a + b }")
      val addTwoInts = new AddTwoInts(steps)
      addTwoInts.fitness must equal(addTwoInts.maxFitness)
    }

    "1 add 1 equals 2 with TypeTree that converts nodes to raw Scala" in {
      val steps = new TypeTree(
        Seq(
          Object(Seq(
            FunctionM(
              params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM())),
              nodes = Seq(
                AddOperator(ValueRef("a"), ValueRef("b"))
              ), name = "f0")),
            name = "o0")))
      val f = new AddTwoInts(steps)
      f.fitness must equal(f.maxFitness)
    }

    "return score less than max fitness for a non-optimal solution" in {
      // Arrange
      val steps = new TypeTree(List(Object(List(FunctionM(List(ValDclInFunctionParam("v0", IntegerM()), ValDclInFunctionParam("v1", IntegerM())), List(ValueRef("v0")), "f0")), "o0")))

      // Act
      val addTwoInts = new AddTwoInts(steps)

      // Assert
      addTwoInts.fitness < addTwoInts.maxFitness must equal(true)
    }
  }
}