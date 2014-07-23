package models.domain.scala

import com.google.inject.Injector
import models.domain.common.Node
import nodes._
import nodes.helpers.{IScope, UpdateScopeIncrementObjects}
import scala.annotation.tailrec

final case class ObjectDef(nodes: Seq[Node], name: String) extends Node with UpdateScopeIncrementObjects {

  override def toRaw: String = s"object $name ${nodes.map(f => f.toRaw).mkString("{ ", " ", " }")}"

  override def validate(scope: IScope): Boolean = if (scope.hasHeightRemaining) {
    nodes.forall {
      case n: FunctionM => n.validate(scope.decrementHeight)
      case _: Empty => false
      case _ => false
    }
  }
  else false

  override def replaceEmpty(scope: IScope, injector: Injector): Node = {
    def funcCreateNodes(scope: IScope, injector: Injector, premade: Seq[Node]): (IScope, Seq[Node]) = {
      val factory = injector.getInstance(classOf[ObjectDefFactoryImpl])
      factory.createNodes(scope = scope, acc = premade.init)
    }

    @tailrec
    def replaceEmptyInSeq(scope: IScope,
                          injector: Injector,
                          n: Seq[Node],
                          f: ((IScope, Injector, Seq[Node]) => (IScope, Seq[Node])),
                          acc: Seq[Node] = Seq.empty): (IScope, Seq[Node]) = {
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

    val (_, n) = replaceEmptyInSeq(scope, injector, nodes, funcCreateNodes)
    ObjectDef(n, name)
  }

  override def getMaxDepth: Int = 1 + nodes.map(_.getMaxDepth).reduceLeft(math.max)
}
