package composition

import com.google.inject.AbstractModule
import memoization.LookupChildrenWithFutures
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import utils.PozInt

import scala.concurrent.Future

final class StubLookupChildrenWithFutures(size: Int = 0) extends AbstractModule with MockitoSugar {

  val stub = {
    val lookupChildren: LookupChildrenWithFutures = mock[LookupChildrenWithFutures]
    when(lookupChildren.get(any[IScope], any[PozInt])).thenReturn(Future.successful(Set.empty[PozInt]))
    when(lookupChildren.size).thenReturn(size)
    lookupChildren
  }

  def configure(): Unit = bind(classOf[LookupChildrenWithFutures]).toInstance(stub)
}