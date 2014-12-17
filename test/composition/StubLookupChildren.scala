package composition

import com.google.inject.AbstractModule
import memoization.LookupChildren
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import utils.PozInt
import scala.concurrent.Future

final class StubLookupChildren(lookupChildren: LookupChildren = mock(classOf[LookupChildren])) extends AbstractModule {

  def configure(): Unit = {
    when(lookupChildren.fetch(any[IScope], any[PozInt])).thenReturn(Future.successful(Set.empty[PozInt]))
    bind(classOf[LookupChildren]).toInstance(lookupChildren)
  }
}