package replaceEmpty

import com.google.inject.Inject
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.IntegerM
import scala.async.Async.async
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class IntegerMFactoryImpl @Inject()() extends IntegerMFactory with UpdateScopeNoChange {

  override val nodesToChooseFrom = Seq.empty

  override def create(scope: IScope): Future[Instruction] = async {
    IntegerM()
  }
}

object IntegerMFactoryImpl {

  val id = 3
}