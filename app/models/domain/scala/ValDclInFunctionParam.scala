package models.domain.scala

import nodes.{IntegerMFactory, UpdateScopeIncrementVals, Node}
import nodes.helpers.IScope
import com.google.inject.Injector

case class ValDclInFunctionParam(name: String, primitiveType: Node) extends Node with UpdateScopeIncrementVals {
  override def toRaw: String = s"$name: ${primitiveType.toRaw}"

  override def validate(scope: IScope): Boolean = scope.hasDepthRemaining && !name.isEmpty && {
    primitiveType match {
      case p: IntegerM => p.validate(scope)
      case _ => false
    }
  }

  override def replaceEmpty(scope: IScope, injector: Injector): Node = {
    val p = primitiveType match {
      case p: Empty => injector.getInstance(classOf[IntegerMFactory]).create(scope)
      case p: Node => p.replaceEmpty(updateScope(scope.incrementDepth), injector)
    }
    ValDclInFunctionParam(name, p)
  }

  override def getMaxDepth = 1 + primitiveType.getMaxDepth
}
