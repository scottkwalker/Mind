package nodes

import nodes.helpers.Scope

trait UpdateScope {
  def updateScope(scope: Scope): Scope
}

trait UpdateScopeNoChange extends UpdateScope {
  override def updateScope(scope: Scope): Scope = scope
}

trait UpdateScopeIncrementVals extends UpdateScope {
  override def updateScope(scope: Scope): Scope = scope.incrementVals
}

trait UpdateScopeIncrementFuncs extends UpdateScope {
  override def updateScope(scope: Scope): Scope = scope.incrementFuncs
}

trait UpdateScopeIncrementObjects extends UpdateScope {
  override def updateScope(scope: Scope): Scope = scope.incrementObjects
}

trait UpdateScopeThrows extends UpdateScope {
  override def updateScope(scope: Scope): Scope = throw new scala.RuntimeException("Should not happen as you cannot have more than one NodeTree")
}