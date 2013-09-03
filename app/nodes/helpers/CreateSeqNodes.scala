package nodes.helpers

import com.google.inject.Inject
import scala.annotation.tailrec
import nodes.Node
import ai.Ai

case class CreateSeqNodes @Inject() (val createNode: CreateNode) {
  @tailrec
  final def create(parent: CreateChildNodes, scope: Scope, ai: Ai, constraints: (Scope => Boolean), acc: Seq[Node] = Seq[Node]()): Seq[Node] = {
    constraints(scope) match {
      case false => acc
      case true => {
        val (updatedScope, child) = createNode.create(parent, scope, ai)

        create(parent,
          updatedScope.incrementAccumulatorLength,
          ai,
          constraints,
          acc ++ Seq(child))
      }
    }
  }
}