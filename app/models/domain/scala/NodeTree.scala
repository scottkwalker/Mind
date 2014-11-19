package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.{NodeTreeFactory, UpdateScopeThrows}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

final case class NodeTree(nodes: Seq[Instruction]) extends Instruction with UpdateScopeThrows {

  override def toRaw: String = nodes.map(f => f.toRaw).mkString(" ")

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining && {
    nodes.forall {
      case n: ObjectDef => n.hasNoEmpty(scope.decrementHeight)
      case _: Empty => false
      case _ => false
    }
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Instruction = {
    def replaceEmpty(scope: IScope, head: Instruction) = {
      head match {
        case _: Empty => factory.createNodes(scope = scope) // Head node (and any nodes after it) is of type empty, so replace it with a non-empty
        case n: Instruction =>
          Future.successful {
            val r = n.replaceEmpty(scope) // Head node is not empty, but one of the child nodes may be so check it's children.
            val u = r.updateScope(scope) // Update scope to include this node.
            (u, Seq(r))
          }
      }
    }

    require(nodes.length > 0, "must not be empty as then we have nothing to replace")
    lazy val factory = injector.getInstance(classOf[NodeTreeFactory])
    val seqWithoutEmpties = nodes.foldLeft(Future.successful((scope, Seq.empty[Instruction]))) {
      (fAcc, instruction) => fAcc.flatMap {
        case (updatedScope, acc) => replaceEmpty(scope = updatedScope, head = instruction)
      }
    }
    val (_, n) = Await.result(seqWithoutEmpties, utils.Timeout.finiteTimeout)
    NodeTree(n)
  }

  override def height: Int = 1 + nodes.map(_.height).max
}