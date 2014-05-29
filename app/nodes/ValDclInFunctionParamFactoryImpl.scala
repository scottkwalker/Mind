package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import models.domain.scala.ValDclInFunctionParam
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours

trait ValDclInFunctionParamFactory extends ICreateChildNodes

case class ValDclInFunctionParamFactoryImpl @Inject()(injector: Injector,
                                                      creator: ICreateNode,
                                                      legalNeighbours: LegalNeighbours
                                                       ) extends ValDclInFunctionParamFactory with UpdateScopeIncrementVals {
  override val neighbourIds = Seq(IntegerMFactory.id)

  override def create(scope: IScope): Node = {
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