package models.domain.scala

import com.google.inject.Injector
import replaceEmpty.{FunctionMFactoryImpl, UpdateScopeIncrementFuncs}
import models.common.IScope
import models.domain.Instruction
import scala.annotation.tailrec

final case class FunctionM(params: Seq[Instruction],
                           nodes: Seq[Instruction],
                           name: String) extends Instruction with UpdateScopeIncrementFuncs {

  override def toRaw: String = {
    require(!name.isEmpty)
    s"def $name${params.map(f => f.toRaw).mkString("(", ", ", ")")} = ${nodes.map(f => f.toRaw).mkString("{ ", " ", " }")}"
  }

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining &&
    !name.isEmpty &&
    nodes.forall(n => n.hasNoEmpty(scope.decrementHeight))

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Instruction = {
    def funcCreateParams(scope: IScope, premade: Seq[Instruction]): (IScope, Seq[Instruction]) = {
      val factory = injector.getInstance(classOf[FunctionMFactoryImpl])
      factory.createParams(scope = scope, acc = premade.init)
    }

    def funcCreateNodes(scope: IScope, premade: Seq[Instruction]): (IScope, Seq[Instruction]) = {
      val factory = injector.getInstance(classOf[FunctionMFactoryImpl])
      factory.createNodes(scope = scope, acc = premade.init)
    }

    val (updatedScope, p) = replaceEmptyInSeq(scope, params, funcCreateParams)
    val (_, n) = replaceEmptyInSeq(updatedScope, nodes, funcCreateNodes)

    FunctionM(p, n, name)
  }

  override def height: Int = {
    def height(n: Seq[Instruction]): Int = n.map(_.height).reduceLeft(math.max)
    1 + math.max(height(params), height(nodes))
  }

  @tailrec
  private def replaceEmptyInSeq(scope: IScope, n: Seq[Instruction], f: ((IScope, Seq[Instruction]) => (IScope, Seq[Instruction])), acc: Seq[Instruction] = Seq.empty)(implicit injector: Injector): (IScope, Seq[Instruction]) = {
    n match {
      case x :: xs =>
        val (updatedScope, replaced) = x match {
          case _: Empty => f(scope, n)
          case n: Instruction =>
            val r = n.replaceEmpty(scope)
            val u = r.updateScope(scope)
            (u, Seq(r))
        }
        replaceEmptyInSeq(updatedScope, xs, f, acc ++ replaced)
      case nil => (scope, acc)
    }
  }
}

object FunctionM {

  def apply(params: Seq[Instruction],
            nodes: Seq[Instruction],
            index: Int): FunctionM = FunctionM(params, nodes, name = s"f$index")
}