package factory

import ai.SelectionStrategy
import com.google.inject.Inject
import models.common.IScope
import models.domain.Node
import models.domain.scala.ValueRef

final case class ValueRefFactoryImpl @Inject()(ai: SelectionStrategy) extends ValueRefFactory with UpdateScopeNoChange {

  override val neighbourIds = Seq.empty

  override def create(scope: IScope): Node = {
    val name = "v" + ai.chooseIndex(scope.numVals)
    ValueRef(name = name)
  }
}

object ValueRefFactoryImpl {

  val id = 7
}