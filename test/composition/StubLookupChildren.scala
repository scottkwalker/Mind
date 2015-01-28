package composition

import com.google.inject.AbstractModule
import memoization.LookupChildren
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import utils.PozInt

final class StubLookupChildren(lookupChildren: LookupChildren = mock(classOf[LookupChildren]), size: Int = 0) extends AbstractModule {

  def build = {
    when(lookupChildren.get(any[IScope], any[PozInt])).thenReturn(Set.empty[PozInt])
    when(lookupChildren.size).thenReturn(size)
    lookupChildren
  }

  def configure(): Unit = {

    bind(classOf[LookupChildren]).toInstance(build)
  }
}