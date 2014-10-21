package replaceEmpty

import com.google.inject.Inject
import memoization.LookupNeighbours
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.ValDclInFunctionParam

case class ValDclInFunctionParamFactoryImpl @Inject()(
                                                       creator: CreateNode,
                                                       legalNeighbours: LookupNeighbours
                                                       ) extends ValDclInFunctionParamFactory with UpdateScopeIncrementVals {

  override val neighbourIds = Seq(IntegerMFactoryImpl.id)

  override def create(scope: IScope): Instruction = {
    val name = "v" + scope.numVals
    val ln = legalNeighbours.fetch(scope, neighbourIds)
    val (_, primitiveType) = creator.create(ln, scope)

    ValDclInFunctionParam(name = name,
      primitiveType = primitiveType) // TODO need to make more types.
  }
}

object ValDclInFunctionParamFactoryImpl {

  val id = 6
}