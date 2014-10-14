package models.domain.scala

import com.google.inject.Injector
import replaceEmpty.{ObjectDefFactoryImpl, UpdateScopeIncrementObjects}
import models.common.IScope
import models.domain.Node
import scala.annotation.tailrec

final case class ObjectDef(nodes: Seq[Node], name: String) extends Node with UpdateScopeIncrementObjects {

  override def toRaw: String = s"object $name ${nodes.map(f => f.toRaw).mkString("{ ", " ", " }")}"

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining && {
    nodes.forall {
      case n: FunctionM => n.hasNoEmpty(scope.decrementHeight)
      case _: Empty => false
      case _ => false
    }
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Node = {
    def funcCreateNodes(scope: IScope, premade: Seq[Node]): (IScope, Seq[Node]) = {
      val factory = injector.getInstance(classOf[ObjectDefFactoryImpl])
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
    ObjectDef(n, name)
  }

  override def height: Int = 1 + nodes.map(_.height).reduceLeft(math.max)
}

object ObjectDef {

  def apply(nodes: Seq[Node], index: Int): ObjectDef = ObjectDef(
    nodes = nodes,
    name = s"o$index"
  )
}