package models.domain.scala

import nodes._
import nodes.helpers.{UpdateScopeIncrementFuncs, IScope}
import com.google.inject.Injector
import scala.annotation.tailrec
import models.domain.common.Node

final case class FunctionM(params: Seq[Node],
                     nodes: Seq[Node],
                     name: String) extends Node with UpdateScopeIncrementFuncs {
  override def toRaw: String = {
    require(!name.isEmpty)
    s"def $name${params.map(f => f.toRaw).mkString("(", ", ", ")")} = ${nodes.map(f => f.toRaw).mkString("{ ", " ", " }")}"
  }

  override def validate(scope: IScope): Boolean = if (scope.hasDepthRemaining) !name.isEmpty &&
    nodes.forall(n => n.validate(scope.incrementDepth))
  else false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = {
    def funcCreateParams(scope: IScope, injector: Injector, premade: Seq[Node]): (IScope, Seq[Node]) = {
      val factory = injector.getInstance(classOf[FunctionMFactoryImpl])
      factory.createParams(scope = scope, acc = premade.init)
    }

    def funcCreateNodes(scope: IScope, injector: Injector, premade: Seq[Node]): (IScope, Seq[Node]) = {
      val factory = injector.getInstance(classOf[FunctionMFactoryImpl])
      factory.createNodes(scope = scope, acc = premade.init)
    }

    val (updatedScope, p) = replaceEmptyInSeq(scope, injector, params, funcCreateParams)
    val (_, n) = replaceEmptyInSeq(updatedScope, injector, nodes, funcCreateNodes)

    FunctionM(p, n, name)
  }

  @tailrec
  private def replaceEmptyInSeq(scope: IScope, injector: Injector, n: Seq[Node], f: ((IScope, Injector, Seq[Node]) => (IScope, Seq[Node])), acc: Seq[Node] = Seq.empty): (IScope, Seq[Node]) = {
    n match {
      case x :: xs =>
        val (updatedScope, replaced) = x match {
          case _: Empty => f(scope, injector, n)
          case n: Node =>
            val r = n.replaceEmpty(scope, injector)
            val u = r.updateScope(scope)
            (u, Seq(r))
        }
        replaceEmptyInSeq(updatedScope, injector, xs, f, acc ++ replaced)
      case nil => (scope, acc)
    }
  }

  override def getMaxDepth: Int = {
    def getMaxDepth(n: Seq[Node]): Int = n.map(_.getMaxDepth).reduceLeft(math.max)
    1 + math.max(getMaxDepth(params), getMaxDepth(nodes))
  }
}