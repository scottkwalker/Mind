package nodes.helpers

import com.google.inject.Inject
import scala.annotation.tailrec
import nodes.Node
import ai.Ai

case class CreateSeqNodes @Inject()(createNode: CreateNode) {
  @tailrec
  final def createSeq(possibleChildren: Seq[CreateChildNodes],
                      scope: Scope,
                      ai: Ai,
                      constraints: ((Scope, Int) => Boolean),
                      saveAccLengthInScope: ((Scope, Int) => Scope) = (scope, accLength) => scope,
                      acc: Seq[Node] = Seq[Node]()): (Scope, Seq[Node]) = {
    constraints(scope, acc.length) match {
      case false => {
        val updatedScope = saveAccLengthInScope(scope, acc.length)
        (updatedScope, acc)
      }
      case true => {
        val (updatedScope, child) = createNode.create(possibleChildren, scope, ai)

        createSeq(possibleChildren,
          updatedScope,
          ai,
          constraints,
          saveAccLengthInScope,
          acc ++ Seq(child))
      }
    }
  }
}