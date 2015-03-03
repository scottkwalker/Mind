package composition

import com.google.inject.AbstractModule
import composition.StubFactoryLookupAnyBinding._
import decision.AccumulateInstructions
import decision.Decision
import models.common.IScope
import models.domain.Step
import models.domain.scala.FactoryLookup
import org.mockito.Matchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import utils.PozInt

import scala.concurrent.Future

final class StubFactoryLookupAnyBinding extends AbstractModule with MockitoSugar {

  val stubDecision = {
    val decision = mock[Decision]
    when(decision.nodesToChooseFrom).thenReturn(Set.empty[PozInt])
    val step = mock[Step]
    when(decision.createStep(any[IScope])).thenReturn(Future.successful(step))
    val accumulateInstructions = mock[AccumulateInstructions]
    when(accumulateInstructions.instructions).thenReturn(Seq(step))
    when(decision.createParams(any[IScope])).thenReturn(Future.successful(accumulateInstructions))
    when(decision.createNodes(any[IScope])).thenReturn(Future.successful(accumulateInstructions))
    decision
  }

  val stub = {
    val factoryLookup: FactoryLookup = mock[FactoryLookup]
    // Id -> factory
    when(factoryLookup.convert(fakeFactoryDoesNotTerminateId)).thenReturn(fNot)
    when(factoryLookup.convert(fakeFactoryTerminates1Id)).thenReturn(fakeFactoryTerminates1)
    when(factoryLookup.convert(fakeFactoryTerminates2Id)).thenReturn(fakeFactoryTerminates2)
    when(factoryLookup.convert(fakeFactoryHasChildrenId)).thenReturn(fakeFactoryHasChildren)
    when(factoryLookup.convert(any[PozInt])).thenReturn(stubDecision)
    // Factory -> Id
    when(factoryLookup.convert(fakeFactoryTerminates1)).thenReturn(fakeFactoryTerminates1Id)
    when(factoryLookup.convert(fakeFactoryTerminates2)).thenReturn(fakeFactoryTerminates2Id)
    when(factoryLookup.factories).thenReturn(Set(fakeFactoryDoesNotTerminateId, fakeFactoryTerminates1Id, fakeFactoryTerminates2Id, fakeFactoryHasChildrenId))
    factoryLookup
  }

  override def configure(): Unit = bind(classOf[FactoryLookup]).toInstance(stub)
}

object StubFactoryLookupAnyBinding {

  val numberOfFactories = 4
  val fakeFactoryDoesNotTerminateId = PozInt(100)
  val fakeFactoryTerminates1Id = PozInt(101)
  val fakeFactoryTerminates2Id = PozInt(102)
  val fakeFactoryHasChildrenId = PozInt(103)
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