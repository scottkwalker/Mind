package nodes

import nodes.helpers._
import com.google.inject.Inject
import ai.IAi
import models.domain.scala.IntegerM
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours

case class IntegerMFactory @Inject()() extends ICreateChildNodes with UpdateScopeNoChange {
  override val neighbourIds = Seq.empty

  override def create(scope: IScope): Node = IntegerM()
}

object IntegerMFactory {
  val id = 3
}