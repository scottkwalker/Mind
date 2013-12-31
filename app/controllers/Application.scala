package controllers

import play.api.mvc._
import com.google.inject.{Guice, Injector}
import nodes.helpers._
import ai.randomWalk.RandomWalkModule
import nodes.helpers.Scope
import nodes.NodeTreeFactory
import scala.collection.mutable
import scala.annotation.tailrec

object Application extends Controller {

  def index = Action {
    val injector: Injector = Guice.createInjector(new DevModule, new RandomWalkModule)
    val factory = injector.getInstance(classOf[NodeTreeFactory])

    val scope = Scope(maxExpressionsInFunc = 1,
      maxFuncsInObject = 1,
      maxParamsInFunc = 1,
      maxDepth = 5,
      maxObjectsInTree = 1)



    val result = populateMemoizationMap(factory, scope, mutable.Map.empty)

    Ok(<p>Hello, World! ${result}</p>)
  }

  @tailrec
  private def populateMemoizationMap(factory: ICreateChildNodes,
                                     scope: IScope,
                                     acc: mutable.Map[IScope, Boolean]): mutable.Map[IScope, Boolean] = {
    scope.hasDepthRemaining match {
      case true => {
        def calc = factory.canTerminateInStepsRemaining(scope)

        populateMemoizationMap(factory, scope.incrementDepth, acc.updated(scope, calc))
      }
      case false => acc
    }
  }
}