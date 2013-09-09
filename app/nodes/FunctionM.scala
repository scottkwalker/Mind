package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai

case class FunctionM(params: Seq[ValueInFunctionParam] = Seq(ValueInFunctionParam("a", IntegerM()), ValueInFunctionParam("b", IntegerM())), // TODO these need to be created by the factory.
                     nodes: Seq[Node],
                     name: String) extends Node {
  override def toRawScala: String = {
    require(!name.isEmpty)
    s"def $name${params.map(f => f.toRawScala).mkString("(", ", ", ")")} = ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  }

  override def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) {
    false
  }
  else {
    !name.isEmpty &&
      nodes.forall(_ match {
        case n: AddOperator => n.validate(scope.decrementStepsRemaining)
        case n: ValueRef => n.validate(scope.decrementStepsRemaining)
        case _: Empty => false
        case _ => false
      })
  }
}

case class FunctionMFactory @Inject()(injector: Injector,
                                      creator: CreateSeqNodes,
                                      ai: Ai) extends CreateChildNodes {
  val neighbours: Seq[CreateChildNodes] = Seq(
    injector.getInstance(classOf[AddOperatorFactory]),
    injector.getInstance(classOf[ValueRefFactory]))

  override def create(scope: Scope): Node = {
    val nodes = creator.create(this, scope.resetAccumulator, ai, seqConstraints)

    FunctionM(nodes = nodes, name = "f" + scope.numFuncs)
  }

  override def updateScope(scope: Scope) = scope.incrementFuncs

  private def seqConstraints(scope: Scope): Boolean = scope.funcHasSpaceForChildren
}