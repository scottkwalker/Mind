package models.domain.scala

import nodes._
import nodes.helpers.IScope
import com.google.inject.Injector
import scala.annotation.tailrec
import nodes.Empty

case class ObjectDef(nodes: Seq[Node], name: String) extends Node with UpdateScopeIncrementObjects {
  override def toRawScala: String = s"object $name ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"

  override def validate(scope: IScope): Boolean = if (scope.hasDepthRemaining) {
    nodes.forall {
      case n: FunctionM => n.validate(scope.incrementDepth)
      case _: Empty => false
      case _ => false
    }
  }
  else false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = {
    val (_, n) = replaceEmptyInSeq(scope, injector, nodes, funcCreateNodes)
    ObjectDef(n, name)
  }

  private def funcCreateNodes(scope: IScope, injector: Injector, premade: Seq[Node]): (IScope, Seq[Node]) = {
    val factory = injector.getInstance(classOf[ObjectDefFactory])
    factory.createNodes(scope = scope, acc = premade.init)
  }

  @tailrec
  private def replaceEmptyInSeq(scope: IScope, injector: Injector, n: Seq[Node], f: ((IScope, Injector, Seq[Node]) => (IScope, Seq[Node])), acc: Seq[Node] = Seq[Node]()): (IScope, Seq[Node]) = {
    n match {
      case x :: xs =>
        val (updatedScope, replaced) = x match {
          case _: Empty =>
            f(scope, injector, n)
          case n: Node =>
            val r = n.replaceEmpty(scope, injector)
            val u = r.updateScope(scope)
            (u, Seq(r))
        }
        replaceEmptyInSeq(updatedScope, injector, xs, f, acc ++ replaced)
      case nil => (scope, acc)
    }
  }

  override def getMaxDepth: Int = 1 + nodes.map(_.getMaxDepth).reduceLeft(math.max)
}