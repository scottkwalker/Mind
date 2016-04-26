package decision

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.IScope
import models.domain.Step
import models.domain.scala.ValDclInFunctionParam

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class ValDclInFunctionParamFactoryImpl @Inject()(
    creator: CreateNode,
    lookupChildren: LookupChildrenWithFutures
)
    extends ValDclInFunctionParamFactory with UpdateScopeIncrementVals {

  override val nodesToChooseFrom = Set(IntegerMFactory.id)

  override def fillEmptySteps(scope: IScope): Future[Step] = {
    val name = "v" + scope.numVals
    val ln = lookupChildren.get(scope, nodesToChooseFrom)
    creator.create(ln, scope).map {
      case (_, primitiveType) =>
        ValDclInFunctionParam(name, primitiveType) // TODO need to make more types.
    }
  }

  override def createParams(scope: IScope): Future[AccumulateInstructions] =
    throw new RuntimeException(
        "calling this method is not possible as there will be no params")

  override def createNodes(scope: IScope): Future[AccumulateInstructions] =
    throw new RuntimeException(
        "calling this method is not possible as there will be no child nodes")
}
