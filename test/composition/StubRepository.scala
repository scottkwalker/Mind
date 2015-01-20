package composition

import com.google.inject.AbstractModule
import memoization.RepositoryWithFutures
import org.mockito.Mockito._

final class StubRepository(val repositoryWithFutures: RepositoryWithFutures) extends AbstractModule {

  def configure(): Unit = {
    val repositoryWithFutures = mock(classOf[RepositoryWithFutures])
    when(repositoryWithFutures.size).thenReturn(0)
    bind(classOf[RepositoryWithFutures]).toInstance(repositoryWithFutures)
  }
}