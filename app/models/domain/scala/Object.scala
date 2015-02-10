package models.domain.scala

import com.google.inject.Injector
import models.common.IScope
import models.domain.Step
import decision.AccumulateInstructions
import decision.ObjectFactory
import decision.UpdateScopeIncrementObjects

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class Object(nodes: Seq[Step], name: String) extends Step with UpdateScopeIncrementObjects {

  override def toCompilable: String = s"object $name ${nodes.map(f => f.toCompilable).mkString("{ ", " ", " }")}"

  override def hasNoEmptySteps(scope: IScope): Boolean = scope.hasHeightRemaining && {
    nodes.forall {
      case n: FunctionM => n.hasNoEmptySteps(scope.decrementHeight)
      case _: Empty => false
      case _ => false
    }
  }

  private def fillEmptySteps(scope: IScope, currentInstruction: Step, acc: Seq[Step], factoryLookup: FactoryLookup) = {
    def decision = factoryLookup.convert(ObjectFactory.id)
    currentInstruction match {
      case _: Empty => decision.createNodes(scope = scope) // Head node (and any nodes after it) is of type empty, so replace it with a non-empty
      case instruction: Step =>
        instruction.fillEmptySteps(scope = scope, factoryLookup = factoryLookup).map { r => // Head node is not empty, but one of the child nodes may be so check it's children.
          val updatedScope = r.updateScope(scope) // Update scope to include this node.
          AccumulateInstructions(instructions = acc :+ r, scope = updatedScope)
        }
    }
  }

  override def fillEmptySteps(scope: IScope, factoryLookup: FactoryLookup): Future[Step] = async {
    require(nodes.length > 0, "must not be empty as then we have nothing to replace")
    val fNodesWithoutEmpties = nodes.foldLeft(Future.successful(AccumulateInstructions(instructions = Seq.empty[Step], scope = scope))) {
      (previousResult, currentInstruction) => previousResult.flatMap { previous =>
        fillEmptySteps(scope = previous.scope, currentInstruction = currentInstruction, acc = previous.instructions, factoryLookup = factoryLookup)
      }
    }
    val nodesWithoutEmpties = await(fNodesWithoutEmpties)
    Object(nodesWithoutEmpties.instructions, name)
  }

  override def height: Int = 1 + nodes.map(_.height).reduceLeft(math.max)
}

object Object {

  def apply(nodes: Seq[Step], index: Int): Object = Object(
    nodes = nodes,
    name = s"o$index"
  )
}