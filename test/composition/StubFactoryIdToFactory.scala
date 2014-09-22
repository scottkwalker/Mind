package composition

import com.google.inject.AbstractModule
import composition.StubFactoryIdToFactory._
import factory.ReplaceEmpty
import memoization.FactoryLookup
import org.mockito.Mockito.{mock, when}

final class StubFactoryIdToFactory(factoryIdToFactory: FactoryLookup) extends AbstractModule {

  def configure(): Unit = {
    // Id -> factory
    when(factoryIdToFactory.convert(fakeFactoryDoesNotTerminateId)).thenReturn(fNot)
    when(factoryIdToFactory.convert(fakeFactoryTerminates1Id)).thenReturn(fakeFactoryTerminates1)
    when(factoryIdToFactory.convert(fakeFactoryTerminates2Id)).thenReturn(fakeFactoryTerminates2)
    when(factoryIdToFactory.convert(fakeFactoryHasChildrenId)).thenReturn(fakeFactoryHasChildren)
    // Factory -> Id
    when(factoryIdToFactory.convert(fakeFactoryTerminates1)).thenReturn(fakeFactoryTerminates1Id)
    when(factoryIdToFactory.convert(fakeFactoryTerminates2)).thenReturn(fakeFactoryTerminates2Id)

    bind(classOf[FactoryLookup]).toInstance(factoryIdToFactory)
  }
}

object StubFactoryIdToFactory {

  val fakeFactoryDoesNotTerminateId = 0
  val fakeFactoryTerminates1Id = 1
  val fakeFactoryTerminates2Id = 2
  val fakeFactoryHasChildrenId = 3
  val fakeFactoryTerminates1 = {
    val factory = mock(classOf[ReplaceEmpty])
    when(factory.neighbourIds).thenReturn(Seq.empty)
    factory
  }
  val fakeFactoryTerminates2 = {
    val factory = mock(classOf[ReplaceEmpty])
    when(factory.neighbourIds).thenReturn(Seq.empty)
    factory
  }
  val fNot: ReplaceEmpty = {
    val fakeFactoryDoesNotTerminate = mock(classOf[ReplaceEmpty])
    when(fakeFactoryDoesNotTerminate.neighbourIds).thenReturn(Seq(fakeFactoryDoesNotTerminateId))
    fakeFactoryDoesNotTerminate
  }
  val fakeFactoryHasChildren: ReplaceEmpty = {
    val fakeFactoryDoesNotTerminate = mock(classOf[ReplaceEmpty])
    when(fakeFactoryDoesNotTerminate.neighbourIds).thenReturn(Seq(fakeFactoryTerminates1Id, fakeFactoryTerminates2Id))
    fakeFactoryDoesNotTerminate
  }
}