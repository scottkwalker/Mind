package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.{AccumulateInstructions, NodeTreeFactory, UpdateScopeThrows}

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class NodeTree(nodes: Seq[Instruction]) extends Instruction with UpdateScopeThrows {

  override def toRaw: String = nodes.map(f => f.toRaw).mkString(" ")

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining && {
    nodes.forall {
      case n: ObjectDef => n.hasNoEmpty(scope.decrementHeight)
      case _: Empty => false
      case _ => false
    }
  }

  private def replaceEmpty(scope: IScope, currentInstruction: Instruction, acc: Seq[Instruction])(implicit injector: Injector) = {
    lazy val factory = injector.getInstance(classOf[NodeTreeFactory])
    currentInstruction match {
      case _: Empty => factory.createNodes(scope = scope) // Head node (and any nodes after it) is of type empty, so replace it with a non-empty
      case instruction: Instruction =>
        instruction.replaceEmpty(scope).map { r => // Head node is not empty, but one of the child nodes may be so check it's children.
          val updatedScope = r.updateScope(scope) // Update scope to include this node.
          AccumulateInstructions(instructions = acc :+ r, scope = updatedScope)
        }
    }
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Future[Instruction] = async {
    require(nodes.length > 0, "must not be empty as then we have nothing to replace")
    val fNodesWithoutEmpties = nodes.foldLeft(Future.successful(AccumulateInstructions(instructions = Seq.empty[Instruction], scope = scope))) {
      (previousResult, currentInstruction) => previousResult.flatMap { previous =>
        replaceEmpty(scope = previous.scope, currentInstruction = currentInstruction, acc = previous.instructions)
      }
    }
    val nodesWithoutEmpties = await(fNodesWithoutEmpties)
    NodeTree(nodesWithoutEmpties.instructions)
  }

  override def height: Int = 1 + nodes.map(_.height).max
}