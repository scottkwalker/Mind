package models.domain.scala

import com.google.inject.Injector
import factory.{NodeTreeFactoryImpl, UpdateScopeThrows}
import models.common.IScope
import models.domain.Node
import scala.annotation.tailrec

final case class NodeTree(nodes: Seq[Node]) extends Node with UpdateScopeThrows {

  override def toRaw: String = nodes.map(f => f.toRaw).mkString(" ")

  override def hasNoEmpty(scope: IScope): Boolean = if (scope.hasHeightRemaining) {
    nodes.forall {
      case n: ObjectDef => n.hasNoEmpty(scope.decrementHeight)
      case _: Empty => false
      case _ => false
    }
  }
  else false

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Node = {
    def funcCreateNodes(scope: IScope, premade: Seq[Node]): (IScope, Seq[Node]) = {
      val factory = injector.getInstance(classOf[NodeTreeFactoryImpl])
      factory.createNodes(scope = scope, acc = premade.init)
    }

    @tailrec
    def replaceEmptyInSeq(scope: IScope,
                          n: Seq[Node],
                          f: ((IScope, Seq[Node]) => (IScope, Seq[Node])),
                          acc: Seq[Node] = Seq.empty)(implicit injector: Injector): (IScope, Seq[Node]) = {
      n match {
        case x :: xs =>
          val (updatedScope, replaced) = x match {
            case _: Empty =>
              f(scope, n)
            case n: Node =>
              val r = n.replaceEmpty(scope)
              val u = r.updateScope(scope)
              (u, Seq(r))
          }
          replaceEmptyInSeq(updatedScope, xs, f, acc ++ replaced)
        case nil => (scope, acc)
      }
    }

    val (_, n) = replaceEmptyInSeq(scope, nodes, funcCreateNodes)
    NodeTree(n)
  }

  override def getMaxDepth: Int = 1 + nodes.map(_.getMaxDepth).reduceLeft(math.max)
}