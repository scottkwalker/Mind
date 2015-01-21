package composition

import com.google.inject.AbstractModule
import memoization.{Memoize2Impl, RepositoryWithFutures, Memoize2, LookupChildrenWithFutures}
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import utils.PozInt
import scala.concurrent.Future

final class StubLookupChildren(lookupChildren: LookupChildrenWithFutures = mock(classOf[LookupChildrenWithFutures]), size: Int = 0) extends AbstractModule {

  def configure(): Unit = {
    when(lookupChildren.fetch(any[IScope], any[PozInt])).thenReturn(Future.successful(Set.empty[PozInt]))
    when(lookupChildren.size).thenReturn(size)
    bind(classOf[LookupChildrenWithFutures]).toInstance(lookupChildren)
  }
}