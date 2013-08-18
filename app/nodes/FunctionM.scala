package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule
import com.google.inject.Inject

case class FunctionM(val nodes: Seq[Node], val name: String = "f0") extends Node {
  def toRawScala: String = s"def ${name}${params.mkString("(", ", ", ")")} = ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  val params: Seq[String] = Seq("a: Int", "b: Int") // TODO these need to be created by the factory.

  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) { false }
  else {
    !name.isEmpty &&
      nodes.forall(_ match {
        case n: AddOperator => n.validate(stepsRemaining - 1)
        case n: ValueRef => n.validate(stepsRemaining - 1)
        case _: Empty => false
        case _ => false
      })
  }
}

case class FunctionMFactory @Inject() (injector: Injector) extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(
    injector.getInstance(classOf[AddOperatorFactory]),
    injector.getInstance(classOf[ValueRefFactory]))

  def create(scope: Scope): Node = FunctionM(Seq(allPossibleChildren(0).create(scope)), name = "f" + scope.numFuncs)
}