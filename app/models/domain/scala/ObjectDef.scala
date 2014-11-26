package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.{ObjectDefFactory, UpdateScopeIncrementObjects}

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class ObjectDef(nodes: Seq[Instruction], name: String) extends Instruction with UpdateScopeIncrementObjects {

  override def toRaw: String = s"object $name ${nodes.map(f => f.toRaw).mkString("{ ", " ", " }")}"

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining && {
    nodes.forall {
      case n: FunctionM => n.hasNoEmpty(scope.decrementHeight)
      case _: Empty => false
      case _ => false
    }
  }

  private def replaceEmpty(scope: IScope, currentInstruction: Instruction, acc: Seq[Instruction])(implicit injector: Injector) = {
    lazy val factory = injector.getInstance(classOf[ObjectDefFactory])
    currentInstruction match {
      case _: Empty => factory.createNodes(scope = scope) // Head node (and any nodes after it) is of type empty, so replace it with a non-empty
      case instruction: Instruction =>
        instruction.replaceEmpty(scope).map { r => // Head node is not empty, but one of the child nodes may be so check it's children.
          val updatedScope = r.updateScope(scope) // Update scope to include this node.
          (updatedScope, acc :+ r)
        }
    }
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Future[Instruction] = async {
    require(nodes.length > 0, "must not be empty as then we have nothing to replace")
    val seqWithoutEmpties = nodes.foldLeft(Future.successful((scope, Seq.empty[Instruction]))) {
      (previousResult, currentInstruction) => previousResult.flatMap {
        case (updatedScope, acc) => replaceEmpty(scope = updatedScope, currentInstruction = currentInstruction, acc = acc)
      }
    }
    val (_, n) = await(seqWithoutEmpties)
    ObjectDef(n, name)
  }

  override def height: Int = 1 + nodes.map(_.height).reduceLeft(math.max)
}

object ObjectDef {

  def apply(nodes: Seq[Instruction], index: Int): ObjectDef = ObjectDef(
    nodes = nodes,
    name = s"o$index"
  )
}