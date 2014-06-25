package nodes

import com.google.inject.Inject
import models.domain.common.Node
import models.domain.scala.IntegerM
import nodes.helpers._

case class IntegerMFactory @Inject()() extends ICreateChildNodes with UpdateScopeNoChange {
  override val neighbourIds = Seq.empty

  override def create(scope: IScope): Node = IntegerM()
}

object IntegerMFactory {
  val id = 3
}