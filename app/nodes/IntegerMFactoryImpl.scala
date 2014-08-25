package nodes

import com.google.inject.Inject
import models.common.{IScope, Node}
import models.domain.scala.IntegerM
import nodes.helpers._

trait IntegerMFactory extends ICreateChildNodes

case class IntegerMFactoryImpl @Inject()() extends IntegerMFactory with UpdateScopeNoChange {

  override val neighbourIds = Seq.empty

  override def create(scope: IScope): Node = IntegerM()
}

object IntegerMFactoryImpl {

  final val id = 3
}