package replaceEmpty

import com.google.inject.Inject
import memoization.LookupChildren
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.ValDclInFunctionParam
import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ValDclInFunctionParamFactoryImpl @Inject()(
                                                       creator: CreateNode,
                                                       lookupChildren: LookupChildren
                                                       ) extends ValDclInFunctionParamFactory with UpdateScopeIncrementVals {

  override val nodesToChooseFrom = Seq(IntegerMFactoryImpl.id)

  override def create(scope: IScope): Future[Instruction] = async {
    val name = "v" + scope.numVals
    val ln = lookupChildren.fetch(scope, nodesToChooseFrom)
    val (_, primitiveType) = await(creator.create(ln, scope))

    ValDclInFunctionParam(name = name,
      primitiveType = primitiveType) // TODO need to make more types.
  }
}

object ValDclInFunctionParamFactoryImpl {

  val id = 6
}