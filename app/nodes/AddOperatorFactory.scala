package nodes

import nodes.helpers._
import com.google.inject.Injector
import com.google.inject.Inject
import ai.IAi
import models.domain.scala.AddOperator
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours


case class AddOperatorFactory @Inject()(injector: Injector,
                                        creator: ICreateNode,
                                        ai: IAi
                                         ) extends ICreateChildNodes with UpdateScopeNoChange {
  override val neighbours = Seq(injector.getInstance(classOf[ValueRefFactory]))
  override val neighbours2 = Seq(ValueRefFactory.id)

  override def create(scope: IScope): Node = {
    val legalNeighbours = injector.getInstance(classOf[LegalNeighbours])
    val ln = legalNeighbours.fetch(scope, neighbours2)
    val (updatedScope, leftChild) = creator.create(ln, scope, ai)
    val (_, rightChild) = creator.create(ln, updatedScope, ai)
    AddOperator(left = leftChild,
      right = rightChild)
  }
}

object AddOperatorFactory {
  val id = 1
}