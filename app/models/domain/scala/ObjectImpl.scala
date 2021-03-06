package models.domain.scala

import decision.AccumulateInstructions
import decision.ObjectFactory
import models.common.IScope
import models.domain.Step

import scala.async.Async.async
import scala.async.Async.await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class ObjectImpl(nodes: Seq[Step], name: String) extends Object {

  override def toCompilable: String =
    s"object $name ${nodes.map(f => f.toCompilable).mkString("{ ", " ", " }")}"

  override def hasNoEmptySteps(scope: IScope): Boolean =
    scope.hasHeightRemaining && {
      nodes.forall {
        case n: FunctionM => n.hasNoEmptySteps(scope.decrementHeight)
        case _: Empty => false
        case _ => throw new RuntimeException("unhandled node type")
      }
    }

  private def fillEmptySteps(scope: IScope,
                             currentInstruction: Step,
                             acc: Seq[Step],
                             factoryLookup: FactoryLookup) = {
    def decision = factoryLookup.convert(ObjectFactory.id)
    currentInstruction match {
      case _: Empty =>
        decision.createNodes(scope = scope) // Head node (and any nodes after it) is of type empty, so replace it with a non-empty
      case instruction: Step =>
        instruction
          .fillEmptySteps(scope = scope, factoryLookup = factoryLookup)
          .map { r =>
            // Head node is not empty, but one of the child nodes may be so check it's children.
            val updatedScope =
              r.updateScope(scope) // Update scope to include this node.
            AccumulateInstructions(
                instructions = acc :+ r, scope = updatedScope)
          }
    }
  }

  override def fillEmptySteps(
      scope: IScope, factoryLookup: FactoryLookup): Future[Step] = {
    require(
        nodes.nonEmpty, "must not be empty as then we have nothing to replace")
    val init = Future.successful(
        AccumulateInstructions(instructions = Seq.empty[Step], scope = scope))
    val fNodesWithoutEmpties = nodes.foldLeft(init) {
      (previousResult, currentInstruction) =>
        previousResult.flatMap { previous =>
          fillEmptySteps(scope = previous.scope,
                         currentInstruction = currentInstruction,
                         acc = previous.instructions,
                         factoryLookup = factoryLookup)
        }
    }
    fNodesWithoutEmpties.map(nodesWithoutEmpties =>
          ObjectImpl(nodesWithoutEmpties.instructions, name))
  }

  override def height: Int = 1 + nodes.map(_.height).reduceLeft(math.max)
}

object ObjectImpl {

  def apply(nodes: Seq[Step], index: Int): ObjectImpl = ObjectImpl(
      nodes = nodes,
      name = s"o$index"
  )
}
