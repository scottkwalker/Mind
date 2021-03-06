package decision

import com.google.inject.Inject
import memoization.LookupChildrenWithFutures
import models.common.IScope
import models.domain.Step
import models.domain.scala.TypeTree

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

final case class TypeTreeFactoryImpl @Inject()(
    creator: CreateSeqNodes,
    lookupChildren: LookupChildrenWithFutures
)
    extends TypeTreeFactory with UpdateScopeThrows {

  override val nodesToChooseFrom = Set(ObjectFactory.id)

  override def fillEmptySteps(
      scope: IScope, premadeChildren: Seq[Decision]): Future[Step] =
    createNodes(scope).flatMap { generatedNodes =>
      val fPremadeWithoutEmpties = premadeChildren.map(
          p => p.fillEmptySteps(scope)) // TODO doesn't the scope need to be updated each pass
      Future.sequence(fPremadeWithoutEmpties).map { premadeWithoutEmpties =>
        val nodes = generatedNodes.instructions ++ premadeWithoutEmpties
        TypeTree(nodes)
      }
    }

  override def createNodes(scope: IScope): Future[AccumulateInstructions] = {
    val acc: Seq[Step] = Seq.empty[Step]
    creator.create(
        possibleChildren = lookupChildren.get(scope, nodesToChooseFrom),
        scope = scope,
        acc = acc,
        factoryLimit = scope.maxObjectsInTree
    )
  }

  override def fillEmptySteps(scope: IScope): Future[Step] =
    createNodes(scope).map(nodes => TypeTree(nodes.instructions))

  override def createParams(scope: IScope): Future[AccumulateInstructions] =
    throw new RuntimeException(
        "calling this method is not possible as there will be no params")
}
