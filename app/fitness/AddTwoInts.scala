package fitness

import models.domain.Step

final class AddTwoInts(val typeTree: Step) extends Fitness {

  override val maxFitness = 1.0d

  override def fitness: Double = {
    val left = 1
    val right = 2
    val expected = 3

    val evaluateAsScala = new Eval
    evaluateAsScala.compile(typeTree.toRaw)

    val result = evaluateAsScala.inPlace[Int](s"o0.f0($left, $right)")

    val delta = (result - expected).abs

    if (delta == 0.0d) maxFitness else 1.0d / delta
  }
}