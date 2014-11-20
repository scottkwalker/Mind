package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Instruction
import replaceEmpty.{ObjectDefFactory, UpdateScopeIncrementObjects}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

final case class ObjectDef(nodes: Seq[Instruction], name: String) extends Instruction with UpdateScopeIncrementObjects {

  override def toRaw: String = s"object $name ${nodes.map(f => f.toRaw).mkString("{ ", " ", " }")}"

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining && {
    nodes.forall {
      case n: FunctionM => n.hasNoEmpty(scope.decrementHeight)
      case _: Empty => false
      case _ => false
    }
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Instruction = {
    def replaceEmpty(scope: IScope, head: Instruction, acc: Seq[Instruction]) = {
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

    require(nodes.length > 0, "must not be empty as then we have nothing to replace")
    lazy val factory = injector.getInstance(classOf[ObjectDefFactory])
    val seqWithoutEmpties = nodes.foldLeft(Future.successful((scope, Seq.empty[Instruction]))) {
      (fAcc, instruction) => fAcc.flatMap {
        case (updatedScope, acc) => replaceEmpty(scope = updatedScope, head = instruction, acc = acc)
      }
    }
    val (_, n) = Await.result(seqWithoutEmpties, utils.Timeout.finiteTimeout)
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