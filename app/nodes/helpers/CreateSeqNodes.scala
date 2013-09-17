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
                      saveAccLengthInScope: Option[((Scope, Int) => Scope)] = None,
                      acc: Seq[Node] = Seq[Node](),
                      premade: Option[Node]): (Scope, Seq[Node]) = {
    constraints(scope, acc.length) match {
      case false => {
        val updatedScope = saveAccLengthInScope match {
          case Some(s) => s(scope, acc.length)
          case None => scope
        }

        (updatedScope, acc)
      }
      case true => {
        val (updatedScope, child) = createNode.create(possibleChildren, scope, ai, premade)

        createSeq(possibleChildren,
          updatedScope,
          ai,
          constraints,
          saveAccLengthInScope,
          acc ++ Seq(child),
          premade)
      }
    }
  }
}