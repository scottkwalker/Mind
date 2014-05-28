package nodes

import nodes.helpers._
import com.google.inject.Inject
import ai.{IRandomNumberGenerator, IAi}
import models.domain.scala.ValueRef
import models.domain.common.Node
import nodes.legalNeighbours.LegalNeighbours

final case class ValueRefFactory @Inject()(ai: IAi,
                                     rng: IRandomNumberGenerator,
                                     legalNeighbours: LegalNeighbours
                                      ) extends ICreateChildNodes with UpdateScopeNoChange {
  override val neighbours = Seq.empty // No possible children
  override val neighbours2 = Seq.empty

  override def create(scope: IScope): Node = {
    val name = "v" + ai.chooseIndex(scope.numVals)
    ValueRef(name = name)
  }
}

object ValueRefFactory {
  val id = 7
}