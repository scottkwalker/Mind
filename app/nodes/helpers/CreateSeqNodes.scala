package nodes.helpers

import com.google.inject.Inject
import scala.annotation.tailrec
import nodes.Node
import ai.Ai
import scala.util.Random

case class CreateSeqNodes @Inject()(createNode: CreateNode, rng: Random, ai: Ai) {
  @tailrec
  final def createSeq(possibleChildren: Seq[CreateChildNodes],
                      scope: Scope,
                      saveAccLengthInScope: Option[((Scope, Int) => Scope)] = None,
                      acc: Seq[Node] = Seq[Node](),
                      factoryLimit: Int
                       ): (Scope, Seq[Node]) = {
    ai.canAddAnother(acc.length, factoryLimit, rng) match {
      case false => {
        val updatedScope = saveAccLengthInScope match {
          case Some(f) => f(scope, acc.length)
          case None => scope
        }
        (updatedScope, acc)
      }
      case true => {
        val (updatedScope, child) = createNode.create(possibleChildren, scope, ai)

        createSeq(possibleChildren,
          updatedScope,
          saveAccLengthInScope,
          acc ++ Seq(child),
          factoryLimit
        )
      }
    }
  }
}