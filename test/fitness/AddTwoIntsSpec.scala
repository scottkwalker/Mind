package fitness

import composition.TestComposition
import models.domain.Instruction
import models.domain.scala.{AddOperator, FunctionM, IntegerM, Object, _}
import org.mockito.Mockito._

final class AddTwoIntsSpec extends TestComposition {

  "Addition" must {
    "1 add 1 equals 2 with TypeTree that returns hard coded raw Scala" in {
      val typeTree = mock[Instruction]
      when(typeTree.toRaw).thenReturn("object o0 { def f0(a: Int, b: Int) = a + b }")
      val f = new AddTwoInts(typeTree)
      f.fitness must equal(f.maxFitness)
    }

    "1 add 1 equals 2 with TypeTree that converts nodes to raw Scala" in {
      val typeTree = new TypeTree(
        Seq(
          Object(Seq(
            FunctionM(
              params = Seq(ValDclInFunctionParam("a", IntegerM()), ValDclInFunctionParam("b", IntegerM())),
              nodes = Seq(
                AddOperator(ValueRef("a"), ValueRef("b"))
              ), name = "f0")),
            name = "o0")))
      val f = new AddTwoInts(typeTree)
      f.fitness must equal(f.maxFitness)
    }

    "return score less than max fitness for a non-optimal solution" in {
      // Arrange
      val typeTree = new TypeTree(List(Object(List(FunctionM(List(ValDclInFunctionParam("v0", IntegerM()), ValDclInFunctionParam("v1", IntegerM())), List(ValueRef("v0")), "f0")), "o0")))

      // Act
      val f = new AddTwoInts(typeTree)

      // Assert
      f.fitness < f.maxFitness must be(true)
    }
  }
}