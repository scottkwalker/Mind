package decision

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.IScope
import models.domain.Step

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class CreateSeqNodesImpl @Inject()(
    createNode: CreateNode, ai: SelectionStrategy)
    extends CreateSeqNodes {

  def create(
      possibleChildren: => Future[Set[Decision]],
      initScope: IScope,
      initAcc: Seq[Step], // Default the accumulator to empty.
      factoryLimit: Int
  ): Future[AccumulateInstructions] = {
    // Create a seq of nodes (of a random length) from a pool of possible children.
    val lengthOfSeq = ai.generateLengthOfSeq(factoryLimit) - initAcc.length
    val init = Future.successful(
        AccumulateInstructions(instructions = initAcc, scope = initScope))
    (1 to lengthOfSeq).foldLeft(init) { (previousResult, _) =>
      previousResult.flatMap { prev =>
        createNode.create(possibleChildren, prev.scope).map {
          case (updatedScope, instruction) =>
            AccumulateInstructions(
                instructions = prev.instructions :+ instruction,
                scope = updatedScope)
        }
      }
    }
  }
}
