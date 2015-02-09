package composition

import com.google.inject.AbstractModule
import composition.StubFactoryLookupBinding._
import models.domain.scala.FactoryLookup
import org.mockito.Mockito.mock
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import decision.Decision
import utils.PozInt

final class StubFactoryLookupBinding extends AbstractModule with MockitoSugar {

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

  override def configure(): Unit = bind(classOf[FactoryLookup]).toInstance(stub)
}

object StubFactoryLookupBinding {

  val numberOfFactories = 4
  val fakeFactoryDoesNotTerminateId = PozInt(0)
  val fakeFactoryTerminates1Id = PozInt(1)
  val fakeFactoryTerminates2Id = PozInt(2)
  val fakeFactoryHasChildrenId = PozInt(3)
  val fakeFactoryTerminates1 = {
    val decision = mock(classOf[Decision])
    when(decision.nodesToChooseFrom).thenReturn(Set.empty[PozInt])
    decision
  }
  val fakeFactoryTerminates2 = {
    val decision = mock(classOf[Decision])
    when(decision.nodesToChooseFrom).thenReturn(Set.empty[PozInt])
    decision
  }
  val fNot: Decision = {
    val decision = mock(classOf[Decision])
    when(decision.nodesToChooseFrom).thenReturn(Set(fakeFactoryDoesNotTerminateId))
    decision
  }
  val fakeFactoryHasChildren: Decision = {
    val decision = mock(classOf[Decision])
    when(decision.nodesToChooseFrom).thenReturn(Set(fakeFactoryTerminates1Id, fakeFactoryTerminates2Id))
    decision
  }
}