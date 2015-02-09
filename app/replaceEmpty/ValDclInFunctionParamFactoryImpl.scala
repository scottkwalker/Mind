package replaceEmpty

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.ValDclInFunctionParam
import utils.PozInt
import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ValDclInFunctionParamFactoryImpl @Inject()(
                                                       creator: CreateNode,
                                                       lookupChildren: LookupChildrenWithFutures
                                                       ) extends ValDclInFunctionParamFactory with UpdateScopeIncrementVals {

  override val nodesToChooseFrom = Set(IntegerMFactory.id)

  override def create(scope: IScope): Future[Instruction] = async {
    val name = "v" + scope.numVals
    val ln = lookupChildren.get(scope, nodesToChooseFrom)
    val (_, primitiveType) = await(creator.create(ln, scope))
    ValDclInFunctionParam(name = name,
      primitiveType = primitiveType) // TODO need to make more types.
  }
}