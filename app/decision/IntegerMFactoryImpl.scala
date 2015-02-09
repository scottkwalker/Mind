package decision

import com.google.inject.Inject
import models.common.IScope
import models.domain.Step
import models.domain.scala.IntegerM
import utils.PozInt

import scala.async.Async.async
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class IntegerMFactoryImpl @Inject()() extends IntegerMFactory with UpdateScopeNoChange {

  override val nodesToChooseFrom = Set.empty[PozInt]

  override def createStep(scope: IScope): Future[Step] = async {
    IntegerM()
  }
}