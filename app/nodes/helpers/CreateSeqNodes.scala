package nodes.helpers

import com.google.inject.Inject
import scala.annotation.tailrec
import nodes.Node
import ai.Ai

case class CreateSeqNodes @Inject() (createNode: CreateNode) {
  @tailrec
  final def create(possibleChildren: Seq[CreateChildNodes],
                   scope: Scope,
                   ai: Ai,
                   constraints: ((Scope, Int) => Boolean),
                   updateScopeWithAcc: ((Scope, Int) => Scope) = (s, _) => s,
                   acc: Seq[Node] = Seq[Node]()): (Scope, Seq[Node]) = {
    constraints(scope, acc.length) match {
      case false => (updateScopeWithAcc(scope, acc.length), acc)
      case true => {
        val (updatedScope, child) = createNode.create(possibleChildren, scope, ai)

        create(possibleChildren,
          updatedScope,
          ai,
          constraints,
          updateScopeWithAcc,
          acc ++ Seq(child))
      }
    }
  }
}