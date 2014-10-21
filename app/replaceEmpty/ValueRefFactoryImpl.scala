package replaceEmpty

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.IScope
import models.domain.Instruction
import models.domain.scala.ValueRef

final case class ValueRefFactoryImpl @Inject()(ai: SelectionStrategy) extends ValueRefFactory with UpdateScopeNoChange {

  override val neighbourIds = Seq.empty

  override def create(scope: IScope): Instruction = ValueRef(index = ai.chooseIndex(scope.numVals))
}

object ValueRefFactoryImpl {

  val id = 7
}