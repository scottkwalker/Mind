package composition

import com.google.inject.AbstractModule
import memoization.LookupNeighbours
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import scala.concurrent.Future

final class StubLookupNeighbours(lookupNeighbours: LookupNeighbours = mock(classOf[LookupNeighbours])) extends AbstractModule {

  def configure(): Unit = {
    when(lookupNeighbours.fetch(any[IScope], any[Int])).thenReturn(Future.successful(Seq.empty))
    bind(classOf[LookupNeighbours]).toInstance(lookupNeighbours)
  }
}