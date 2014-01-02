package nodes.helpers

import nodes.{Empty, Node}

trait ICreateChildNodes {
  val neighbours: Seq[ICreateChildNodes]

  val mapOfCanTerminateInStepsRemaining: IMemoizeDi[IScope, Boolean]

  val mapOfLegalNeigbours: IMemoizeDi[IScope, Seq[ICreateChildNodes]]

  def create(scope: IScope): Node = Empty()

  def create(scope: IScope, premade: Option[Seq[ICreateChildNodes]]): Node = Empty()

  def updateScope(scope: IScope): IScope

  /*
    val legalNeighbours: IScope => Seq[ICreateChildNodes] = {
      def inner(f: IScope => Seq[ICreateChildNodes])(scope: IScope): Seq[ICreateChildNodes] = {
        neighbours.filter(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
      }
      Memoize.Y(inner)
    }
  */
  def legalNeighbours(scope: IScope): Seq[ICreateChildNodes] = {
    def calc = neighbours.filter(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
    mapOfLegalNeigbours.store getOrElseUpdate(scope, calc)
  }

  /*
    protected val canTerminateInStepsRemaining: IScope => Boolean = {
      def inner(f: IScope => Boolean)(scope: IScope): Boolean = {
        scope.hasDepthRemaining match {
          case true => neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
          case false => false
        }
      }
      Memoize.Y(inner)
    }
  */

  def canTerminateInStepsRemaining(scope: IScope): Boolean = {
    def calc = scope.hasDepthRemaining match {
      case true => neighbours.exists(n => n.canTerminateInStepsRemaining(scope.incrementDepth))
      case false => false
    }
    mapOfCanTerminateInStepsRemaining.store getOrElseUpdate(scope, calc)
  }

  def populateMemoizationMaps(maxVals: Int,
                              maxFuncs: Int,
                              numObjects: Int,
                              maxExpressionsInFunc: Int,
                              maxFuncsInObject: Int,
                              maxParamsInFunc: Int,
                              maxDepth: Int,
                              maxObjectsInTree: Int): Unit = {}
}