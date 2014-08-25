package models.domain.scala

import com.google.inject.Injector
import factory.{FunctionMFactoryImpl, UpdateScopeIncrementFuncs}
import models.common.IScope
import models.domain.Node
import scala.annotation.tailrec

final case class FunctionM(params: Seq[Node],
                           nodes: Seq[Node],
                           name: String) extends Node with UpdateScopeIncrementFuncs {

  override def toRaw: String = {
    require(!name.isEmpty)
    s"def $name${params.map(f => f.toRaw).mkString("(", ", ", ")")} = ${nodes.map(f => f.toRaw).mkString("{ ", " ", " }")}"
  }

  override def validate(scope: IScope): Boolean = if (scope.hasHeightRemaining) !name.isEmpty &&
    nodes.forall(n => n.validate(scope.decrementHeight))
  else false

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Node = {
    def funcCreateParams(scope: IScope, premade: Seq[Node]): (IScope, Seq[Node]) = {
      val factory = injector.getInstance(classOf[FunctionMFactoryImpl])
      factory.createParams(scope = scope, acc = premade.init)
    }

    def funcCreateNodes(scope: IScope, premade: Seq[Node]): (IScope, Seq[Node]) = {
      val factory = injector.getInstance(classOf[FunctionMFactoryImpl])
      factory.createNodes(scope = scope, acc = premade.init)
    }

    val (updatedScope, p) = replaceEmptyInSeq(scope, params, funcCreateParams)
    val (_, n) = replaceEmptyInSeq(updatedScope, nodes, funcCreateNodes)

    FunctionM(p, n, name)
  }

  override def getMaxDepth: Int = {
    def getMaxDepth(n: Seq[Node]): Int = n.map(_.getMaxDepth).reduceLeft(math.max)
    1 + math.max(getMaxDepth(params), getMaxDepth(nodes))
  }

  @tailrec
  private def replaceEmptyInSeq(scope: IScope, n: Seq[Node], f: ((IScope, Seq[Node]) => (IScope, Seq[Node])), acc: Seq[Node] = Seq.empty)(implicit injector: Injector): (IScope, Seq[Node]) = {
    n match {
      case x :: xs =>
        val (updatedScope, replaced) = x match {
          case _: Empty => f(scope, n)
          case n: Node =>
            val r = n.replaceEmpty(scope)
            val u = r.updateScope(scope)
            (u, Seq(r))
        }
        replaceEmptyInSeq(updatedScope, xs, f, acc ++ replaced)
      case nil => (scope, acc)
    }
  }
}