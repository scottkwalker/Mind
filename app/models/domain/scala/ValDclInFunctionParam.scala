package models.domain.scala

import com.google.inject.Injector
import replaceEmpty.{IntegerMFactoryImpl, UpdateScopeIncrementVals}
import models.common.IScope
import models.domain.Instruction
import utils.Timeout.finiteTimeout
import scala.concurrent.Await

final case class ValDclInFunctionParam(name: String, primitiveType: Instruction) extends Instruction with UpdateScopeIncrementVals {

  override def toRaw: String = s"$name: ${primitiveType.toRaw}"

  override def hasNoEmpty(scope: IScope): Boolean = scope.hasHeightRemaining && !name.isEmpty && {
    primitiveType match {
      case p: IntegerM => p.hasNoEmpty(scope)
      case _ => false
    }
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Instruction = {
    val pt = primitiveType match {
      case _: Empty => Await.result(injector.getInstance(classOf[IntegerMFactoryImpl]).create(scope), finiteTimeout)
      case node: Instruction =>
        node.replaceEmpty(updateScope(scope.decrementHeight))
    }
    ValDclInFunctionParam(name, pt)
  }

  override def height: Int = 1 + primitiveType.height
}
