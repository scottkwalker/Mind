package composition

import com.google.inject.AbstractModule
import composition.StubFactoryLookup._
import models.domain.scala.FactoryLookup
import org.mockito.Mockito.{mock, when}
import org.scalatest.mock.MockitoSugar
import replaceEmpty.ReplaceEmpty
import utils.PozInt

final class StubFactoryLookup extends AbstractModule with MockitoSugar {

  val stub = {
    val factoryLookup: FactoryLookup = mock[FactoryLookup]
    // Id -> factory
    when(factoryLookup.convert(fakeFactoryDoesNotTerminateId)).thenReturn(fNot)
    when(factoryLookup.convert(fakeFactoryTerminates1Id)).thenReturn(fakeFactoryTerminates1)
    when(factoryLookup.convert(fakeFactoryTerminates2Id)).thenReturn(fakeFactoryTerminates2)
    when(factoryLookup.convert(fakeFactoryHasChildrenId)).thenReturn(fakeFactoryHasChildren)
    // Factory -> Id
    when(factoryLookup.convert(fakeFactoryTerminates1)).thenReturn(fakeFactoryTerminates1Id)
    when(factoryLookup.convert(fakeFactoryTerminates2)).thenReturn(fakeFactoryTerminates2Id)
    when(factoryLookup.factories).thenReturn(Set(fakeFactoryDoesNotTerminateId, fakeFactoryTerminates1Id, fakeFactoryTerminates2Id, fakeFactoryHasChildrenId))
    factoryLookup
  }

  def configure(): Unit = bind(classOf[FactoryLookup]).toInstance(stub)
}

object StubFactoryLookup {

  val numberOfFactories = 4
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