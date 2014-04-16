package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.IAi
import models.domain.scala.ValDclInFunctionParam


case class ValDclInFunctionParamFactory @Inject()(injector: Injector,
                                                  creator: ICreateNode,
                                                  ai: IAi
                                                   ) extends ICreateChildNodes with UpdateScopeIncrementVals {
  val neighbours: Seq[ICreateChildNodes] = Seq(injector.getInstance(classOf[IntegerMFactory]))

  override val canTerminateInStepsRemaining: IScope => Boolean = {
    def inner(f: IScope => Boolean)(scope: IScope): Boolean = scope.hasDepthRemaining
    Memoize.Y(inner)
  }
  /*override def canTerminateInStepsRemaining(scope: IScope): Boolean = {
    val result = scope.hasDepthRemaining
    mapOfCanTerminateInStepsRemaining getOrElseUpdate(scope, result)
  }*/

  override def create(scope: IScope): Node = {
    val name = "v" + scope.numVals

    val (_, primitiveType) = creator.create(legalNeighbours(scope), scope, ai)

    ValDclInFunctionParam(name = name,
      primitiveType = primitiveType) // TODO need to make more types.
  }
}