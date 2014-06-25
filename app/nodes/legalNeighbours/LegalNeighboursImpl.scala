package nodes.legalNeighbours

import java.util.concurrent.CountDownLatch

import com.google.inject.Inject
import nodes.helpers.{ICreateChildNodes, IScope}
import nodes.memoization.Memoize2Impl
import play.api.libs.json.Json._
import play.api.libs.json.{JsValue, Json, Writes}

final class LegalNeighboursImpl @Inject()(implicit intToFactory: FactoryIdToFactory) extends LegalNeighbours {
  val memo = new MemoizeScopeToNeighbours() // TODO we could IoC this

  override def fetch(scope: IScope, neighbours: Seq[Int]): Seq[ICreateChildNodes] = {
    val validForThisScope = memo.apply(key = scope, t2 = neighbours)
    validForThisScope.map(intToFactory.convert)
  }
}