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
             saveAccLengthInScope: Option[((IScope, Int) => IScope)] = None,
             initAcc: Seq[Instruction] = Seq.empty, // Default the accumulator to empty.
             factoryLimit: Int
              ): Future[(IScope, Seq[Instruction])] = {
    (1 to ai.generateLengthOfSeq(factoryLimit) - initAcc.length).foldLeft(Future.successful((initScope, initAcc))) {
      (previous, count) => previous.flatMap {
        case (previousScope, acc) =>
          createNode.create(possibleChildren, previousScope).map {
            case (updatedScope, instruction) => (updatedScope, acc :+ instruction)
          }
      }
    }



//    if (ai.canAddAnother(initAcc.length, factoryLimit)) {
//      createNode.create(possibleChildren, initScope).flatMap {
//        case (updatedScope, child) =>
//          create(possibleChildren,
//            updatedScope,
//            saveAccLengthInScope,
//            initAcc :+ child,
//            factoryLimit
//          )
//      }
//    }
//    else {
//      val updatedScope = saveAccLengthInScope match {
//        case Some(f) => f(initScope, initAcc.length)
//        case None => initScope
//      }
//      Future.successful((updatedScope, initAcc))
//    }

  }
}