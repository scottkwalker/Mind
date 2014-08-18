package models.domain.scala

import com.google.inject.Injector
import models.common.{IScope, Node}
import nodes.IntegerMFactoryImpl
import nodes.helpers.UpdateScopeIncrementVals

final case class ValDclInFunctionParam(name: String, primitiveType: Node) extends Node with UpdateScopeIncrementVals {

  override def toRaw: String = s"$name: ${primitiveType.toRaw}"

  override def validate(scope: IScope): Boolean = scope.hasHeightRemaining && !name.isEmpty && {
    primitiveType match {
      case p: IntegerM => p.validate(scope)
      case _ => false
    }
  }

  override def replaceEmpty(scope: IScope)(implicit injector: Injector): Node = {
    val pt = primitiveType match {
      case _: Empty => injector.getInstance(classOf[IntegerMFactoryImpl]).create(scope)
      case node: Node => node.replaceEmpty(updateScope(scope.decrementHeight))
    }
    ValDclInFunctionParam(name, pt)
  }

  override def getMaxDepth: Int = 1 + primitiveType.getMaxDepth
}
