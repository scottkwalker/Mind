package replaceEmpty

import com.google.inject.Inject
import memoization.LookupNeighbours
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.ValDclInFunctionParam
import utils.Timeout.finiteTimeout
import scala.concurrent.Await

case class ValDclInFunctionParamFactoryImpl @Inject()(
                                                       creator: CreateNode,
                                                       legalNeighbours: LookupNeighbours
                                                       ) extends ValDclInFunctionParamFactory with UpdateScopeIncrementVals {

  override val neighbourIds = Seq(IntegerMFactoryImpl.id)

  override def create(scope: IScope): Instruction = {
    val name = "v" + scope.numVals
    val ln = legalNeighbours.fetch(scope, neighbourIds)
    val (_, primitiveType) = Await.result(creator.create(ln, scope), finiteTimeout)

    ValDclInFunctionParam(name = name,
      primitiveType = primitiveType) // TODO need to make more types.
  }
}

object ValDclInFunctionParamFactoryImpl {

  val id = 6
}