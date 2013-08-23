package nodes

import nodes.helpers.CreateChildNodes
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai

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

  override def create(scope: Scope): Node = {
    val ai = injector.getInstance(classOf[Ai])
    val childFactory = ai.chooseChild(this, scope)
    val updatedScope = childFactory.updateScope(scope: Scope)
    val child = childFactory.create(updatedScope)
    val nodes = Seq(child) // TODO Need to increment the scope in a recursive way each time we create a new child.
    ObjectM(nodes, name = "o" + scope.numObjects)
  }
  
  override def updateScope(scope: Scope) = {
    scope.incrementObjects
  }
  
  // TODO Move to the CreateChildNodes. For creating a seq of nodes from the same level of scope:
  // @TailRecursion
  // def createSeq(scope: Scope, ai: Ai): Seq[Node] = {
  //  val possibleChildren = validChildren(scope)
  //  if (possibleChildren.isEmpty) { Nil }
  //  else {
  //    val nodeFactory = ai.chooseChild(possibleChildren)
  //    val scopeUpdated // Do increment based on which nodeFactory was choosen
  //    val node = nodeFactory.create(scope) 
  //     
  //    return node.create(scope: Scope, ai: Ai) :: create(scopeUpdated, ai) // Check Odersky's adding to Seq recursively
  //  }
}