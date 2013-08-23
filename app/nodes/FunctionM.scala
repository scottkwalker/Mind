package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Guice
import nodes.helpers.DevModule
import com.google.inject.Inject
import ai.Ai

case class FunctionM(val nodes: Seq[Node], val name: String = "f0") extends Node {
  def toRawScala: String = s"def ${name}${params.mkString("(", ", ", ")")} = ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  val params: Seq[String] = Seq("a: Int", "b: Int") // TODO these need to be created by the factory.

  def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) { false }
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

case class FunctionMFactory @Inject() (injector: Injector) extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(
    injector.getInstance(classOf[AddOperatorFactory]),
    injector.getInstance(classOf[ValueRefFactory]))

  override def create(scope: Scope): Node = {
    val ai = injector.getInstance(classOf[Ai])
    val childFactory = ai.chooseChild(this, scope)
    val updatedScope = childFactory.updateScope(scope)
    val child = childFactory.create(updatedScope)
    FunctionM(Seq(child), name = "f" + scope.numFuncs)
  }
  
  override def updateScope(scope: Scope) = {
    scope.incrementFuncs
  }
}