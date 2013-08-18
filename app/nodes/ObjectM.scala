package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Inject

case class ObjectM(val nodes: Seq[Node], val name: String = "Individual") extends Node {
  def toRawScala: String = s"object ${name} ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"

  def validate(stepsRemaining: Integer): Boolean = if (stepsRemaining == 0) false else nodes.forall(n => n match {
    case _: FunctionM => n.validate(stepsRemaining - 1)
    case _: Empty => false
    case _ => false
  })
}

case class ObjectMFactory @Inject() (injector: Injector) extends CreateChildNodes {
  val allPossibleChildren: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[FunctionMFactory]))

  def create(scope: Scope): Node = {
    ObjectM(Seq(allPossibleChildren(0).create(scope)), name = "o" + scope.numObjects)} // Need to increment the scope in a recursive way each time we create a new child.
}