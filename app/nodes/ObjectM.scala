package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Inject

case class ObjectM(val nodes: Seq[Node], val name: String = "Individual") extends Node {
  def toRawScala: String = s"object ${name} ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"

  def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) false else nodes.forall(n => n match {
    case _: FunctionM => n.validate(scope.decrementStepsRemaining)
    case _: Empty => false
    case _ => false
  })
}

case class ObjectMFactory @Inject() (injector: Injector) extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[FunctionMFactory]))

  def create(scope: Scope): Node = {
    val possibleChildren = validChildren(scope)
    val nodes = Seq(allPossibleChildren(0).create(scope)) // TODO Need to increment the scope in a recursive way each time we create a new child.
    ObjectM(nodes, name = "o" + scope.numObjects)
  } 
}