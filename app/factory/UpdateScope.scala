package factory

import models.common.IScope

trait UpdateScope {

  def updateScope(scope: IScope): IScope
}

trait UpdateScopeNoChange extends UpdateScope {

  override def updateScope(scope: IScope): IScope = scope
}

trait UpdateScopeIncrementVals extends UpdateScope {

  override def updateScope(scope: IScope): IScope = scope.incrementVals
}

trait UpdateScopeIncrementFuncs extends UpdateScope {

  override def updateScope(scope: IScope): IScope = scope.incrementFuncs
}

trait UpdateScopeIncrementObjects extends UpdateScope {

  override def updateScope(scope: IScope): IScope = scope.incrementObjects
}

trait UpdateScopeThrows extends UpdateScope {

  override def updateScope(scope: IScope): IScope = throw new scala.RuntimeException("Should not happen as you cannot have more than one NodeTree")
}