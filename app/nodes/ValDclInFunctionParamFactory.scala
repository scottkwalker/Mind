package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.IAi
import models.domain.scala.ValDclInFunctionParam
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours


case class ValDclInFunctionParamFactory @Inject()(injector: Injector,
                                                  creator: ICreateNode
                                                   ) extends ICreateChildNodes with UpdateScopeIncrementVals {
  override val neighbourIds = Seq(IntegerMFactory.id)

  override def create(scope: IScope): Node = {
    val legalNeighbours = injector.getInstance(classOf[LegalNeighbours])
    val name = "v" + scope.numVals
    val ln = legalNeighbours.fetch(scope, neighbourIds)
    val (_, primitiveType) = creator.create(ln, scope)

    ValDclInFunctionParam(name = name,
      primitiveType = primitiveType) // TODO need to make more types.
  }
}

object ValDclInFunctionParamFactory {
  val id = 6
}