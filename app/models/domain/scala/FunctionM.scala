package models.domain.scala

import nodes._
import nodes.helpers.IScope
import com.google.inject.Injector
import scala.annotation.tailrec
import nodes.Empty

case class FunctionM(params: Seq[Node],
                     nodes: Seq[Node],
                     name: String) extends Node with UpdateScopeIncrementFuncs {
  override def toRawScala: String = {
    require(!name.isEmpty)
    s"def $name${params.map(f => f.toRawScala).mkString("(", ", ", ")")} = ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"
  }

  override def validate(scope: IScope): Boolean = if (scope.hasDepthRemaining) {
    !name.isEmpty &&
      nodes.forall {
        case n: AddOperator => n.validate(scope.incrementDepth)
        case n: ValueRef => n.validate(scope.incrementDepth)
        case _: Empty => false
        case _ => false
      }
  }
  else false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = {
    val (updatedScope, p) = replaceEmptyInSeq(scope, injector, params, funcCreateParams)
    val (temp, n) = replaceEmptyInSeq(updatedScope, injector, nodes, funcCreateNodes)

    FunctionM(p, n, name)
  }

  private def funcCreateParams(scope: IScope, injector: Injector, premade: Seq[Node]): (IScope, Seq[Node]) = {
    val factory = injector.getInstance(classOf[FunctionMFactory])
    factory.createParams(scope = scope, acc = premade.init)
  }

  private def funcCreateNodes(scope: IScope, injector: Injector, premade: Seq[Node]): (IScope, Seq[Node]) = {
    val factory = injector.getInstance(classOf[FunctionMFactory])
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

  override def getMaxDepth: Int = 1 + math.max(getMaxDepth(params), getMaxDepth(nodes))

  private def getMaxDepth(n: Seq[Node]): Int = n.map(_.getMaxDepth).reduceLeft(math.max)
}
