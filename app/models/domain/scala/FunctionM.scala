package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.{FunctionMFactory, UpdateScopeIncrementFuncs}

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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


  private def replaceEmpty(scope: IScope, head: Instruction, acc: Seq[Instruction], funcReplaceEmpty: (IScope) => Future[(IScope, Seq[Instruction])])(implicit injector: Injector): Future[(IScope, Seq[Instruction])] = {
    head match {
      case _: Empty => funcReplaceEmpty(scope) // Head node (and any nodes after it) is of type empty, so replace it with a non-empty
      case n: Instruction =>
        n.replaceEmpty(scope).map { r => // Head node is not empty, but one of the child nodes may be so check it's children.
          val updatedScope = r.updateScope(scope) // Update scope to include this node.
          (updatedScope, acc :+ r)
        }
    }
  }

  private def replaceEmpty(scope: IScope, input: Seq[Instruction], funcReplaceEmpty: (IScope) => Future[(IScope, Seq[Instruction])])(implicit injector: Injector): Future[(IScope, Seq[Instruction])] = {
    input.foldLeft(Future.successful((scope, Seq.empty[Instruction]))) {
      (fAcc, instruction) => fAcc.flatMap {
        case (updatedScope, acc) => replaceEmpty(scope = updatedScope, head = instruction, acc = acc, funcReplaceEmpty = funcReplaceEmpty)
      }
    }
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Future[Instruction] = async {
    require(params.length > 0, "must not be empty as then we have nothing to replace")
    require(nodes.length > 0, "must not be empty as then we have nothing to replace")
    lazy val factory = injector.getInstance(classOf[FunctionMFactory])
    val paramSeqWithoutEmpties = replaceEmpty(scope = scope, input = params, funcReplaceEmpty = factory.createParams)
    val (scopeWithParams, p) = await(paramSeqWithoutEmpties)
    val nodeSeqWithoutEmpties = replaceEmpty(scope = scopeWithParams, input = nodes, funcReplaceEmpty = factory.createNodes)
    val (_, n) = await(nodeSeqWithoutEmpties)
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