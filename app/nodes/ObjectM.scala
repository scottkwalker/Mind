package nodes

import nodes.helpers._
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai
import scala.annotation.tailrec

case class ObjectM(val nodes: Seq[Node], val name: String) extends Node {
  override def toRawScala: String = s"object ${name} ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"

  override def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) false else nodes.forall(n => n match {
    case _: FunctionM => n.validate(scope.decrementStepsRemaining)
    case _: Empty => false
    case _ => false
  })
}

case class ObjectMFactory @Inject() (injector: Injector) extends FeasibleNodes {
  val allPossibleChildren: Seq[FeasibleNodes] = Seq(injector.getInstance(classOf[FunctionMFactory]))

  override def create(scope: Scope): Node = {
    val ai = injector.getInstance(classOf[Ai])
    val factory = ai.chooseChild(this, scope)
    val updatedScope = factory.updateScope(scope)
    val child = factory.create(updatedScope)
    val nodes = createSeq(scope, ai, Seq[Node]())

    ObjectM(nodes, name = "o" + scope.numObjects)
  }

  override def updateScope(scope: Scope) = scope.incrementObjects

  @tailrec
  private def createSeq(scope: Scope, ai: Ai, acc: Seq[Node]): Seq[Node] = {
    def constraint(scope: Scope): Boolean = scope.objHasSpaceForChildren
    
    constraint(scope) match {
      case false => acc
      case true => {
        val (updatedScope, child) = create(scope, ai)

        createSeq(updatedScope, ai, acc ++ Seq(child))
      }
    }
  }

  private def create(scope: Scope, ai: Ai): (Scope, Node) = {
    val factory = ai.chooseChild(this, scope)
    val updatedScope = factory.updateScope(scope)
    (updatedScope: Scope, factory.create(updatedScope): Node)
  }
}