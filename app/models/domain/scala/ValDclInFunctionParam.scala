package models.domain.scala

import nodes.IntegerMFactory
import nodes.helpers.{UpdateScopeIncrementVals, IScope}
import com.google.inject.Injector
import models.domain.common.Node

final case class ValDclInFunctionParam(name: String, primitiveType: Node) extends Node with UpdateScopeIncrementVals {
  override def toRaw: String = s"$name: ${primitiveType.toRaw}"

  override def validate(scope: IScope): Boolean = scope.hasDepthRemaining && !name.isEmpty && {
    primitiveType match {
      case p: IntegerM => p.validate(scope)
      case _ => false
    }
  }

  override def replaceEmpty(scope: IScope, injector: Injector): Node = {
    val pt = primitiveType match {
      case _: Empty => injector.getInstance(classOf[IntegerMFactory]).create(scope)
      case node: Node => node.replaceEmpty(updateScope(scope.incrementDepth), injector)
    }
    ValDclInFunctionParam(name, pt)
  }

  override def getMaxDepth: Int = 1 + primitiveType.getMaxDepth
}
