package composition

import com.google.inject.AbstractModule
import composition.StubFactoryLookup._
import models.domain.scala.FactoryLookup
import replaceEmpty.ReplaceEmpty
import org.mockito.Mockito.{mock, when}
import utils.PozInt

final class StubFactoryLookup(factoryLookup: FactoryLookup) extends AbstractModule {

  def configure(): Unit = {
    // Id -> factory
    when(factoryLookup.convert(fakeFactoryDoesNotTerminateId)).thenReturn(fNot)
    when(factoryLookup.convert(fakeFactoryTerminates1Id)).thenReturn(fakeFactoryTerminates1)
    when(factoryLookup.convert(fakeFactoryTerminates2Id)).thenReturn(fakeFactoryTerminates2)
    when(factoryLookup.convert(fakeFactoryHasChildrenId)).thenReturn(fakeFactoryHasChildren)
    // Factory -> Id
    when(factoryLookup.convert(fakeFactoryTerminates1)).thenReturn(fakeFactoryTerminates1Id)
    when(factoryLookup.convert(fakeFactoryTerminates2)).thenReturn(fakeFactoryTerminates2Id)

    bind(classOf[FactoryLookup]).toInstance(factoryLookup)
  }
}

object StubFactoryLookup {

  val fakeFactoryDoesNotTerminateId = PozInt(0)
  val fakeFactoryTerminates1Id = PozInt(1)
  val fakeFactoryTerminates2Id = PozInt(2)
  val fakeFactoryHasChildrenId = PozInt(3)
  val fakeFactoryTerminates1 = {
    val replaceEmpty = mock(classOf[ReplaceEmpty])
    when(replaceEmpty.nodesToChooseFrom).thenReturn(Set.empty[PozInt])
    replaceEmpty
  }
  val fakeFactoryTerminates2 = {
    val replaceEmpty = mock(classOf[ReplaceEmpty])
    when(replaceEmpty.nodesToChooseFrom).thenReturn(Set.empty[PozInt])
    replaceEmpty
  }
  val fNot: ReplaceEmpty = {
    val replaceEmpty = mock(classOf[ReplaceEmpty])
    when(replaceEmpty.nodesToChooseFrom).thenReturn(Set(fakeFactoryDoesNotTerminateId))
    replaceEmpty
  }
  val fakeFactoryHasChildren: ReplaceEmpty = {
    val replaceEmpty = mock(classOf[ReplaceEmpty])
    when(replaceEmpty.nodesToChooseFrom).thenReturn(Set(fakeFactoryTerminates1Id, fakeFactoryTerminates2Id))
    replaceEmpty
  }
}