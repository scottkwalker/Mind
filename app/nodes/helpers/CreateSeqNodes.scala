package nodes.helpers

import com.google.inject.Inject
import scala.annotation.tailrec
import nodes.Node
import ai.{IRandomNumberGenerator, Ai}
import scala.util.Random

case class CreateSeqNodes @Inject()(createNode: CreateNode, rng: IRandomNumberGenerator, ai: Ai) {
  @tailrec
  final def createSeq(possibleChildren: Seq[CreateChildNodes],
                      scope: IScope,
                      saveAccLengthInScope: Option[((IScope, Int) => IScope)] = None,
                      acc: Seq[Node] = Seq[Node](),
                      factoryLimit: Int
                       ): (IScope, Seq[Node]) = {
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