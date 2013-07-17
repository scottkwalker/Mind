package fitness

import com.twitter.util.Eval
import nodes.NodeTree

class Addition(val nodeTree: NodeTree) extends Fitness {
  def fitness: Double = {
    val a = 1
    val b = 1
    val expected = 2

    val eval = new Eval
    eval.compile(nodeTree.rawScala)
    val result = eval.inPlace[Int](s"Individual.f1(${a}, ${b})")

    val delta = (result - expected).abs

    if (delta == 0) 1.0 else 1 / delta
  }
}