package fitness

import com.twitter.util.Eval
import models.domain.common.Node

final class AddTwoInts(val nodeTree: Node) extends Fitness {

  def fitness: Double = {
    val a = 1
    val b = 2
    val expected = 3

    val eval = new Eval
    eval.compile(nodeTree.toRaw)

    val result = eval.inPlace[Int](s"o0.f0($a, $b)")

    val delta = (result - expected).abs

    if (delta == 0) 1.0 else 1 / delta
  }
}