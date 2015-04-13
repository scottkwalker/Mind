package models.domain.scala

import decision.AccumulateInstructions
import decision.TypeTreeFactory
import decision.UpdateScopeThrows
import models.common.IScope
import models.domain.Step

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class TypeTree(nodes: Seq[Step]) extends Step with UpdateScopeThrows {

  override def toCompilable: String = nodes.map(f => f.toCompilable).mkString(" ")

  override def hasNoEmptySteps(scope: IScope): Boolean = scope.hasHeightRemaining && {
    nodes.forall {
      case n: Object => n.hasNoEmptySteps(scope.decrementHeight)
      case _: Empty => false
      case _ => throw new RuntimeException("unhandled node type")
    }
  }

  override def fillEmptySteps(scope: IScope, factoryLookup: FactoryLookup): Future[Step] = async {
    require(nodes.length > 0, "must not be empty as then we have nothing to replace")
    val init = Future.successful(AccumulateInstructions(instructions = Seq.empty[Step], scope = scope))
    val fNodesWithoutEmpties = nodes.foldLeft(init) {
      (previousResult, currentInstruction) => previousResult.flatMap { previous =>
        fillEmptySteps(scope = previous.scope, currentInstruction = currentInstruction, acc = previous.instructions, factoryLookup = factoryLookup)
      }
    }
    val nodesWithoutEmpties = await(fNodesWithoutEmpties)
    TypeTree(nodesWithoutEmpties.instructions)
  }

  private def fillEmptySteps(scope: IScope, currentInstruction: Step, acc: Seq[Step], factoryLookup: FactoryLookup) = {
    def decision = factoryLookup.convert(TypeTreeFactory.id)
    currentInstruction match {
      case _: Empty => decision.createNodes(scope = scope) // Head node (and any nodes after it) is of type empty, so replace it with a non-empty
      case instruction: Step =>
        instruction.fillEmptySteps(scope = scope, factoryLookup = factoryLookup).map { r => // Head node is not empty, but one of the child nodes may be so check it's children.
          val updatedScope = r.updateScope(scope) // Update scope to include this node.
          AccumulateInstructions(instructions = acc :+ r, scope = updatedScope)
        }
    }
  }

  override def height: Int = 1 + nodes.map(_.height).max
}