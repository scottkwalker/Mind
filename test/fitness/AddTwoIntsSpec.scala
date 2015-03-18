package fitness

import composition.UnitTestHelpers
import models.domain.Step
import models.domain.scala.AddOperatorImpl
import models.domain.scala.IntegerM
import models.domain.scala._
import org.mockito.Mockito._

final class AddTwoIntsSpec extends UnitTestHelpers {

  "Addition" must {
    "return expected fitness if given a hard coded string of valid Scala" in {
      val steps = mock[Step]
      when(steps.toCompilable).thenReturn("object o0 { def f0(a: Int, b: Int) = a + b }")
      val addTwoInts = new AddTwoInts(steps)
      addTwoInts.fitness must equal(addTwoInts.maxFitness)
    }

    "return expected fitness if given a TypeTree of valid Scala" in {
      val steps = new TypeTree(
        Seq(
          ObjectImpl(Seq(
            FunctionMImpl(
              params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM())),
              nodes = Seq(
                AddOperatorImpl(ValueRefImpl("a"), ValueRefImpl("b"))
              ), name = "f0")),
            name = "o0")))
      val f = new AddTwoInts(steps)
      f.fitness must equal(f.maxFitness)
    }

    "return score less than max fitness for a non-optimal solution" in {
      // Arrange
      val steps = new TypeTree(List(ObjectImpl(List(FunctionMImpl(List(ValDclInFunctionParam("v0", IntegerM()), ValDclInFunctionParam("v1", IntegerM())), List(ValueRefImpl("v0")), "f0")), "o0")))

      // Act
      val addTwoInts = new AddTwoInts(steps)

      // Assert
      addTwoInts.fitness < addTwoInts.maxFitness must equal(true)
    }
  }
}