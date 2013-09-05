package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule
import com.google.inject.Inject
import ai.Ai
import scala.annotation.tailrec

case class FunctionM(nodes: Seq[Node], name: String) extends Node {
  override def toRawScala: String = s"def ${name}${params.mkString("(", ", ", ")")} = ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  val params: Seq[String] = Seq("a: Int", "b: Int") // TODO these need to be created by the factory.

  override def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) { false }
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

case class FunctionMFactory @Inject() (injector: Injector,
                                       creator: CreateSeqNodes,
                                       ai: Ai) extends CreateChildNodes {
  val neighbours: Seq[CreateChildNodes] = Seq(
    injector.getInstance(classOf[AddOperatorFactory]),
    injector.getInstance(classOf[ValueRefFactory]))

  override def create(scope: Scope): Node = {
    val nodes = creator.create(this, scope, ai, seqConstraints)

    FunctionM(nodes, name = "f" + scope.numFuncs)
  }

  override def updateScope(scope: Scope) = scope.incrementFuncs

  private def seqConstraints(scope: Scope): Boolean = scope.funcHasSpaceForChildren
}