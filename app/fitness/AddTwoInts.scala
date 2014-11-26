package fitness

import models.domain.Instruction

final class AddTwoInts(val nodeTree: Instruction) extends Fitness {

  override val maxFitness = 1.0d

  override def fitness: Double = {
    val left = 1
    val right = 2
    val expected = 3

    val eval = new Eval
    eval.compile(nodeTree.toRaw)

    val result = eval.inPlace[Int](s"o0.f0($left, $right)")

    val delta = (result - expected).abs

    if (delta == 0.0d) maxFitness else 1.0d / delta
  }
}