package composition

import com.google.inject.AbstractModule
import memoization.LookupChildren
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import scala.concurrent.Future

final class StubLookupChildren(lookupChildren: LookupChildren = mock(classOf[LookupChildren])) extends AbstractModule {

  def configure(): Unit = {
    when(lookupChildren.fetch(any[IScope], any[Int])).thenReturn(Future.successful(Seq.empty))
    bind(classOf[LookupChildren]).toInstance(lookupChildren)
  }
}