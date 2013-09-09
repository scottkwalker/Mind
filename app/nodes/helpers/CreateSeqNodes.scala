package nodes.helpers

import com.google.inject.Inject
import scala.annotation.tailrec
import nodes.Node
import ai.Ai

case class CreateSeqNodes @Inject() (createNode: CreateNode) {
  @tailrec
  final def create(possibleChildren: Seq[CreateChildNodes], scope: Scope, ai: Ai, constraints: (Scope => Boolean), acc: Seq[Node] = Seq[Node]()): Seq[Node] = {
    constraints(scope) match {
      case false => acc
      case true => {
        val (updatedScope, child) = createNode.create(possibleChildren, scope, ai)

        create(possibleChildren,
          updatedScope.incrementAccumulatorLength,
          ai,
          constraints,
          acc ++ Seq(child))
      }
    }
  }
}