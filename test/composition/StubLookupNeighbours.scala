package composition

import com.google.inject.AbstractModule
import memoization.LookupNeighbours
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}

final class StubLookupNeighbours(lookupNeighbours: LookupNeighbours = mock(classOf[LookupNeighbours])) extends AbstractModule {

  def configure(): Unit = {
    when(lookupNeighbours.fetch(any[IScope], any[Int])).thenReturn(Seq.empty)
    bind(classOf[LookupNeighbours]).toInstance(lookupNeighbours)
  }
}
