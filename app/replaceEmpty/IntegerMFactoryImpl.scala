package replaceEmpty

import com.google.inject.Inject
import models.common.IScope
import models.domain.Node
import models.domain.scala.IntegerM

case class IntegerMFactoryImpl @Inject()() extends IntegerMFactory with UpdateScopeNoChange {

  override val neighbourIds = Seq.empty

  override def create(scope: IScope): Node = IntegerM()
}

object IntegerMFactoryImpl {

  val id = 3
}