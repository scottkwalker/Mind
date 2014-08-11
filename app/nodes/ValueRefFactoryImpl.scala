package nodes

import ai.SelectionStrategy
import com.google.inject.Inject
import models.domain.common.Node
import models.domain.scala.ValueRef
import nodes.helpers._

trait ValueRefFactory extends ICreateChildNodes

final case class ValueRefFactoryImpl @Inject()(ai: SelectionStrategy) extends ValueRefFactory with UpdateScopeNoChange {

  override val neighbourIds = Seq.empty

  override def create(scope: IScope): Node = {
    val name = "v" + ai.chooseIndex(scope.numVals)
    ValueRef(name = name)
  }
}

object ValueRefFactoryImpl {

  final val id = 7
}