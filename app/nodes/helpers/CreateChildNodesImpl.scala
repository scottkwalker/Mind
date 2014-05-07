package nodes.helpers

abstract class CreateChildNodesImpl extends ICreateChildNodes {
  override def canTerminateInStepsRemaining(scope: IScope): Boolean = {
    if (scope.hasDepthRemaining) neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
    else false
  }
}
