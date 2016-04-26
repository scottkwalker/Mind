package models.domain.scala

import decision.{AccumulateInstructions, FunctionMFactory}
import models.common.IScope
import models.domain.Step

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class FunctionMImpl(
    params: Seq[Step],
    nodes: Seq[Step],
    name: String
)
    extends FunctionM {

  override def toCompilable: String = {
    require(!name.isEmpty)
    s"def $name${params.map(f => f.toCompilable).mkString("(", ", ", ")")} = ${nodes
      .map(f => f.toCompilable)
      .mkString("{ ", " ", " }")}"
  }

  override def hasNoEmptySteps(scope: IScope): Boolean =
    scope.hasHeightRemaining && !name.isEmpty &&
    nodes.forall(n => n.hasNoEmptySteps(scope.decrementHeight))

  private def fillEmptySteps(
      scope: IScope,
      currentInstruction: Step,
      acc: Seq[Step],
      funcReplaceEmpty: (IScope) => Future[AccumulateInstructions],
      factoryLookup: FactoryLookup): Future[AccumulateInstructions] = {
    currentInstruction match {
      case _: Empty =>
        funcReplaceEmpty(scope) // Head node (and any nodes after it) is of type empty, so replace it with a non-empty
      case instruction: Step =>
        instruction
          .fillEmptySteps(scope = scope, factoryLookup = factoryLookup)
          .map { result =>
            // Head node is not empty, but one of the child nodes may be so check it's children.
            val updatedScope =
              result.updateScope(scope) // Update scope to include this node.
            AccumulateInstructions(
                instructions = acc :+ result, scope = updatedScope)
          }
    }
  }

  private def fillEmptySteps(
      initScope: IScope,
      initInstructions: Seq[Step],
      funcReplaceEmpty: (IScope) => Future[AccumulateInstructions],
      factoryLookup: FactoryLookup): Future[AccumulateInstructions] = {
    val init = Future.successful(AccumulateInstructions(
            instructions = Seq.empty[Step], scope = initScope))
    initInstructions.foldLeft(init) { (previousResult, currentInstruction) =>
      previousResult.flatMap { previous =>
        fillEmptySteps(scope = previous.scope,
                       currentInstruction = currentInstruction,
                       acc = previous.instructions,
                       funcReplaceEmpty = funcReplaceEmpty,
                       factoryLookup = factoryLookup)
      }
    }
  }

  override def fillEmptySteps(
      scope: IScope, factoryLookup: FactoryLookup): Future[Step] = {
    require(params.nonEmpty,
            "must not be empty as then we have nothing to replace")
    require(
        nodes.nonEmpty, "must not be empty as then we have nothing to replace")
    def decision = factoryLookup.convert(FunctionMFactory.id)
    fillEmptySteps(initScope = scope,
                   initInstructions = params,
                   funcReplaceEmpty = decision.createParams,
                   factoryLookup = factoryLookup).flatMap {
      paramSeqWithoutEmpties =>
        fillEmptySteps(initScope = paramSeqWithoutEmpties.scope,
                       initInstructions = nodes,
                       funcReplaceEmpty = decision.createNodes,
                       factoryLookup = factoryLookup).map {
          nodeSeqWithoutEmpties =>
            FunctionMImpl(paramSeqWithoutEmpties.instructions,
                          nodeSeqWithoutEmpties.instructions,
                          name)
        }
    }
  }

  override def height: Int = {
    def height(n: Seq[Step]): Int = n.map(_.height).reduceLeft(math.max)
    1 + math.max(height(params), height(nodes))
  }
}

object FunctionMImpl {

  def apply(
      params: Seq[Step],
      nodes: Seq[Step],
      index: Int
  ): FunctionMImpl = FunctionMImpl(params, nodes, name = s"f$index")
}
