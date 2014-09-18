package composition

import com.google.inject.AbstractModule
import memoization.LegalNeighboursMemo
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}

final class StubLegalNeighboursMemo(legalNeighboursMemo: LegalNeighboursMemo = mock(classOf[LegalNeighboursMemo])) extends AbstractModule {

  def configure(): Unit = {
    when(legalNeighboursMemo.fetch(any[IScope], any[Int])).thenReturn(Seq.empty)
    bind(classOf[LegalNeighboursMemo]).toInstance(legalNeighboursMemo)
  }
}
