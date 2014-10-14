package models.domain.scala

import com.google.inject.Injector
import replaceEmpty.{ObjectDefFactoryImpl, UpdateScopeIncrementObjects}
import models.common.IScope
import models.domain.Instruction
import scala.annotation.tailrec

final case class ObjectDef(nodes: Seq[Instruction], name: String) extends Instruction with UpdateScopeIncrementObjects {

  override def toRaw: String = s"object $name ${nodes.map(f => f.toRaw).mkString("{ ", " ", " }")}"

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining && {
    nodes.forall {
      case n: FunctionM => n.hasNoEmpty(scope.decrementHeight)
      case _: Empty => false
      case _ => false
    }
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Instruction = {
    def funcCreateNodes(scope: IScope, premade: Seq[Instruction]): (IScope, Seq[Instruction]) = {
      val factory = injector.getInstance(classOf[ObjectDefFactoryImpl])
      factory.createNodes(scope = scope, acc = premade.init)
    }

    @tailrec
    def replaceEmptyInSeq(scope: IScope,
                          n: Seq[Instruction],
                          f: ((IScope, Seq[Instruction]) => (IScope, Seq[Instruction])),
                          acc: Seq[Instruction] = Seq.empty)(implicit injector: Injector): (IScope, Seq[Instruction]) = {
      n match {
        case x :: xs =>
          val (updatedScope, replaced) = x match {
            case _: Empty =>
              f(scope, n)
            case n: Instruction =>
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

  def apply(nodes: Seq[Instruction], index: Int): ObjectDef = ObjectDef(
    nodes = nodes,
    name = s"o$index"
  )
}