package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.IAi
import models.domain.scala.ValDclInFunctionParam
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours


case class ValDclInFunctionParamFactory @Inject()(injector: Injector,
                                                  creator: ICreateNode,
                                                  ai: IAi
                                                   ) extends ICreateChildNodes with UpdateScopeIncrementVals {
  override val neighbours = Seq(injector.getInstance(classOf[IntegerMFactory]))
  override val neighbours2 = Seq(IntegerMFactory.id)

  override def create(scope: IScope): Node = {
    val legalNeighbours = injector.getInstance(classOf[LegalNeighbours])
    val name = "v" + scope.numVals
    val ln = legalNeighbours.fetch(scope, neighbours2)
    val (_, primitiveType) = creator.create(ln, scope, ai)

    ValDclInFunctionParam(name = name,
      primitiveType = primitiveType) // TODO need to make more types.
  }
}

object ValDclInFunctionParamFactory {
  val id = 6
}