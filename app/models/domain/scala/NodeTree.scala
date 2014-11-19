package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.{NodeTreeFactoryImpl, UpdateScopeThrows}

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

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
    def funcCreateNodes(scope: IScope, premade: Seq[Instruction]) = {
      val factory = injector.getInstance(classOf[NodeTreeFactoryImpl])
      factory.createNodes(scope = scope, acc = premade.init)
    }

    def replaceEmptyInSeq(scope: IScope,
                          instructions: Seq[Instruction],
                          f: ((IScope, Seq[Instruction]) => Future[(IScope, Seq[Instruction])]), // Replaces empties
                          acc: Seq[Instruction] = Seq.empty)(implicit injector: Injector): Future[(IScope, Seq[Instruction])] = {
      // TODO could it be better as a fold?
      instructions match {
        case head :: tail =>
          val emptiesReplaced = head match {
            case _: Empty => f(scope, instructions) // Head node is of type empty, so replace it with a non-empty
            case n: Instruction =>
              Future.successful {
                val r = n.replaceEmpty(scope) // Head node is not empty, but one of the child nodes may be so check it's children.
                val u = r.updateScope(scope) // Update scope to include this node.
                (u, Seq(r))
              }
          }
          emptiesReplaced flatMap {
            case (updatedScope, replaced) => replaceEmptyInSeq(updatedScope, tail, f, acc ++ replaced) // Recurse
          }
        case nil => Future.successful((scope, acc)) // No more in list so return the accumulator.
      }
    }

    val seqWithoutEmpties = replaceEmptyInSeq(scope, nodes, funcCreateNodes)
    val (_, n) = Await.result(seqWithoutEmpties, utils.Timeout.finiteTimeout)
    NodeTree(n)
  }

  override def height: Int = 1 + nodes.map(_.height).max
}