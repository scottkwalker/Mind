package nodes.legalNeighbours

import nodes.helpers.{ICreateChildNodes, IScope}
import com.google.inject.Inject
import nodes.memoization.{Memoize, Memoize1}

final class LegalNeighboursImpl @Inject()(intToFactory: FactoryIdToFactory) extends LegalNeighbours {

  private def legalForScope(scope: IScope, neighbours: Seq[Int]): Seq[Int] = {
    val memo: Memoize1[IScope, Seq[Int]] = {
      def inner(f: Memoize1[IScope, Seq[Int]])(innerScope: IScope): Seq[Int] = {
        if (innerScope.hasDepthRemaining) neighbours.filter {
          neighbourId =>
            val factory = intToFactory.convert(neighbourId)
            factory.neighbourIds.isEmpty || legalForScope(scope = innerScope.incrementDepth, neighbours = factory.neighbourIds).length > 0
        }
        else Seq.empty
      }

      Memoize.Y(inner)
    }

    memo(scope.incrementDepth).intersect(neighbours) // Only return legal moves that are neighbours
  }

  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes] = legalForScope(scope, neighbours).map(intToFactory.convert)
}


