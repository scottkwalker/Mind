package composition

import com.google.inject.AbstractModule
import composition.StubFactoryLookupAnyBinding._
import decision.{AccumulateInstructions, Decision}
import models.common.IScope
import models.domain.Step
import models.domain.scala.FactoryLookup
import org.mockito.Matchers
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
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
    when(factoryLookup.convert(any[PozInt])).thenReturn(stubDecision)
    when(factoryLookup.convert(doesNotTerminateId)).thenReturn(doesNotTerminate)
    when(factoryLookup.convert(leaf1Id)).thenReturn(leaf1)
    when(factoryLookup.convert(leaf2Id)).thenReturn(leaf2)
    when(factoryLookup.convert(hasChildrenThatTerminateId)).thenReturn(hasChildrenThatTerminate)
    // Factory -> Id
    when(factoryLookup.convert(leaf1)).thenReturn(leaf1Id)
    when(factoryLookup.convert(leaf2)).thenReturn(leaf2Id)
    // factories
    when(factoryLookup.factories).thenReturn(Set(doesNotTerminateId, leaf1Id, leaf2Id, hasChildrenThatTerminateId))
    factoryLookup
  }

  override def configure(): Unit = bind(classOf[FactoryLookup]).toInstance(stub)
}

final class StubFactoryLookupBindingBuilder extends AbstractModule with MockitoSugar {

  val stub: FactoryLookup = mock[FactoryLookup]

  def withChildrenThatTerminate = {
    // Id -> factory
    when(stub.convert(Matchers.eq(hasChildrenThatTerminateId))).thenReturn(hasChildrenThatTerminate)
    // factories
    when(stub.factories).thenReturn(Set(hasChildrenThatTerminateId))
    this
  }

  def withLeafs = {
    // Id -> factory
    when(stub.convert(Matchers.eq(leaf1Id))).thenReturn(leaf1)
    when(stub.convert(Matchers.eq(leaf2Id))).thenReturn(leaf2)
    // factories
    when(stub.factories).thenReturn(Set(leaf1Id, leaf2Id))
    this
  }

  def withDoesNotTerminate = {
    // Id -> factory
    when(stub.convert(Matchers.eq(doesNotTerminateId))).thenReturn(doesNotTerminate)
    // factories
    when(stub.factories).thenReturn(Set(doesNotTerminateId))
   this
  }

  override def configure(): Unit = bind(classOf[FactoryLookup]).toInstance(stub)
}

object StubFactoryLookupAnyBinding {

  val numberOfFactories = 4
  val doesNotTerminateId = PozInt(100)
  val leaf1Id = PozInt(101)
  val leaf2Id = PozInt(102)
  val hasChildrenThatTerminateId = PozInt(103)
  val leaf1 = {
    val decision = mock(classOf[Decision])
    when(decision.nodesToChooseFrom).thenReturn(Set.empty[PozInt])
    decision
  }
  val leaf2 = {
    val decision = mock(classOf[Decision])
    when(decision.nodesToChooseFrom).thenReturn(Set.empty[PozInt])
    decision
  }
  val doesNotTerminate: Decision = {
    val decision = mock(classOf[Decision])
    when(decision.nodesToChooseFrom).thenReturn(Set(doesNotTerminateId))
    decision
  }
  val hasChildrenThatTerminate: Decision = {
    val decision = mock(classOf[Decision])
    when(decision.nodesToChooseFrom).thenReturn(Set(leaf1Id, leaf2Id))
    decision
  }
}