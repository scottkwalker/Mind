package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.{FunctionMFactory, UpdateScopeIncrementFuncs}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

final case class FunctionM(params: Seq[Instruction],
                           nodes: Seq[Instruction],
                           name: String) extends Instruction with UpdateScopeIncrementFuncs {

  override def toRaw: String = {
    require(!name.isEmpty)
    s"def $name${params.map(f => f.toRaw).mkString("(", ", ", ")")} = ${nodes.map(f => f.toRaw).mkString("{ ", " ", " }")}"
  }

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining &&
    !name.isEmpty &&
    nodes.forall(n => n.hasNoEmpty(scope.decrementHeight))

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Instruction = {
    def replaceEmptyParams(scope: IScope, head: Instruction, acc: Seq[Instruction]) = {
      head match {
        case _: Empty => factory.createParams(scope = scope) // Head node (and any nodes after it) is of type empty, so replace it with a non-empty
        case n: Instruction =>
          Future.successful {
            val r = n.replaceEmpty(scope) // Head node is not empty, but one of the child nodes may be so check it's children.
            val u = r.updateScope(scope) // Update scope to include this node.
            (u, acc :+ r)
          }
      }
    }

    def replaceEmptyNodes(scope: IScope, head: Instruction, acc: Seq[Instruction]) = {
      head match {
        case _: Empty => factory.createNodes(scope = scope) // Head node (and any nodes after it) is of type empty, so replace it with a non-empty
        case n: Instruction =>
          Future.successful {
            val r = n.replaceEmpty(scope) // Head node is not empty, but one of the child nodes may be so check it's children.
            val u = r.updateScope(scope) // Update scope to include this node.
            (u, acc :+ r)
          }
      }
    }
    require(params.length > 0, "must not be empty as then we have nothing to replace")
    require(nodes.length > 0, "must not be empty as then we have nothing to replace")
    lazy val factory = injector.getInstance(classOf[FunctionMFactory])
    val paramSeqWithoutEmpties = params.foldLeft(Future.successful((scope, Seq.empty[Instruction]))) {
      (fAcc, instruction) => fAcc.flatMap {
        case (updatedScope, acc) => replaceEmptyParams(scope = updatedScope, head = instruction, acc = acc)
      }
    }
    val (scopeWithParams, p) = Await.result(paramSeqWithoutEmpties, utils.Timeout.finiteTimeout)
    val nodeSeqWithoutEmpties = nodes.foldLeft(Future.successful((scopeWithParams, Seq.empty[Instruction]))) {
      (fAcc, instruction) => fAcc.flatMap {
        case (updatedScope, acc) => replaceEmptyNodes(scope = updatedScope, head = instruction, acc = acc)
      }
    }
    val (_, n) = Await.result(nodeSeqWithoutEmpties, utils.Timeout.finiteTimeout)
    FunctionM(p, n, name)
  }

  override def height: Int = {
    def height(n: Seq[Instruction]): Int = n.map(_.height).reduceLeft(math.max)
    1 + math.max(height(params), height(nodes))
  }
}

object FunctionM {

  def apply(params: Seq[Instruction],
            nodes: Seq[Instruction],
            index: Int): FunctionM = FunctionM(params, nodes, name = s"f$index")
}