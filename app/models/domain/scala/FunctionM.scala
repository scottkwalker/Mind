package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Step
import decision.AccumulateInstructions
import decision.FunctionMFactory
import decision.UpdateScopeIncrementFuncs

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class FunctionM(params: Seq[Step],
                           nodes: Seq[Step],
                           name: String) extends Step with UpdateScopeIncrementFuncs {

  override def toRaw: String = {
    require(!name.isEmpty)
    s"def $name${params.map(f => f.toRaw).mkString("(", ", ", ")")} = ${nodes.map(f => f.toRaw).mkString("{ ", " ", " }")}"
  }

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining &&
    !name.isEmpty &&
    nodes.forall(n => n.hasNoEmpty(scope.decrementHeight))

  private def fillEmptySteps(scope: IScope, currentInstruction: Step, acc: Seq[Step], funcReplaceEmpty: (IScope) => Future[AccumulateInstructions])(implicit injector: Injector): Future[AccumulateInstructions] = {
    currentInstruction match {
      case _: Empty => funcReplaceEmpty(scope) // Head node (and any nodes after it) is of type empty, so replace it with a non-empty
      case instruction: Step =>
        instruction.fillEmptySteps(scope).map { result => // Head node is not empty, but one of the child nodes may be so check it's children.
          val updatedScope = result.updateScope(scope) // Update scope to include this node.
          AccumulateInstructions(instructions = acc :+ result, scope = updatedScope)
        }
    }
  }

  private def fillEmptySteps(initScope: IScope, initInstructions: Seq[Step], funcReplaceEmpty: (IScope) => Future[AccumulateInstructions])(implicit injector: Injector): Future[AccumulateInstructions] = {
    initInstructions.foldLeft(Future.successful(AccumulateInstructions(instructions = Seq.empty[Step], scope = initScope))) {
      (previousResult, currentInstruction) => previousResult.flatMap { previous =>
        fillEmptySteps(scope = previous.scope, currentInstruction = currentInstruction, acc = previous.instructions, funcReplaceEmpty = funcReplaceEmpty)
      }
    }
  }

  override def fillEmptySteps(scope: IScope)(implicit injector: Injector): Future[Step] = async {
    require(params.length > 0, "must not be empty as then we have nothing to replace")
    require(nodes.length > 0, "must not be empty as then we have nothing to replace")
    lazy val factory = injector.getInstance(classOf[FunctionMFactory])
    val paramSeqWithoutEmpties = await(fillEmptySteps(initScope = scope, initInstructions = params, funcReplaceEmpty = factory.createParams))
    val nodeSeqWithoutEmpties = await(fillEmptySteps(initScope = paramSeqWithoutEmpties.scope, initInstructions = nodes, funcReplaceEmpty = factory.createNodes))
    FunctionM(paramSeqWithoutEmpties.instructions, nodeSeqWithoutEmpties.instructions, name)
  }

  override def height: Int = {
    def height(n: Seq[Step]): Int = n.map(_.height).reduceLeft(math.max)
    1 + math.max(height(params), height(nodes))
  }
}

object FunctionM {

  def apply(params: Seq[Step],
            nodes: Seq[Step],
            index: Int): FunctionM = FunctionM(params, nodes, name = s"f$index")
}