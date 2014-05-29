package nodes

import nodes.helpers._
import com.google.inject.Inject
import ai.{IRandomNumberGenerator, IAi}
import models.domain.scala.ValueRef
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours

final case class ValueRefFactory @Inject()(ai: IAi
                                      ) extends ICreateChildNodes with UpdateScopeNoChange {
  override val neighbourIds = Seq.empty

  override def create(scope: IScope): Node = {
    val name = "v" + ai.chooseIndex(scope.numVals)
    ValueRef(name = name)
  }
}

object ValueRefFactory {
  val id = 7
}