package nodes

import com.google.inject.Inject
import models.domain.common.Node
import models.domain.scala.IntegerM
import nodes.helpers._

case class IntegerMFactoryImpl @Inject()() extends ICreateChildNodes with UpdateScopeNoChange {
  override val neighbourIds = Seq.empty

  override def create(scope: IScope): Node = IntegerM()
}

object IntegerMFactoryImpl {
  final val id = 3
}