package replaceEmpty

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.{Scope, IScope}
import models.domain.Instruction
import models.domain.scala.Empty
import utils.Timeout.finiteTimeout
import scala.annotation.tailrec
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

final case class CreateSeqNodesImpl @Inject()(createNode: CreateNode, ai: SelectionStrategy) extends CreateSeqNodes {

  def create(possibleChildren: => Future[Seq[ReplaceEmpty]],
             initScope: IScope,
             initAcc: Seq[Instruction] = Seq.empty, // Default the accumulator to empty.
             factoryLimit: Int
              ): Future[AccumulateInstructions] = {
    // Create a seq of nodes (of a random length) from a pool of possible children.
    val lengthOfSeq = ai.generateLengthOfSeq(factoryLimit) - initAcc.length
    val init = Future.successful(AccumulateInstructions(instructions = initAcc, scope = initScope))
    (1 to lengthOfSeq).foldLeft(init) {
      (previousResult, _) => previousResult.flatMap { prev =>
          createNode.create(possibleChildren, prev.scope).map {
            case (updatedScope, instruction) => AccumulateInstructions(instructions = prev.instructions :+ instruction, scope = updatedScope)
          }
      }
    }
  }
}