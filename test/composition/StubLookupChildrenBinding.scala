package composition

import com.google.inject.AbstractModule
import decision.TypeTreeFactory
import decision.ObjectFactory
import decision.FunctionMFactory
import memoization.LookupChildren
import models.common.IScope
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import utils.PozInt

final class StubLookupChildrenBinding(size: Int = 0) extends AbstractModule with MockitoSugar {

  val stub = {
    val lookupChildren: LookupChildren = mock[LookupChildren]
    when(lookupChildren.size).thenReturn(size)
    lookupChildren
  }

  def withNoChildren = {
    when(stub.get(any[IScope], any[PozInt])).thenReturn(Set.empty[PozInt])
    this
  }

  def withChildren = {
    when(stub.get(any[IScope], any[PozInt])).thenReturn(Set(TypeTreeFactory.id, ObjectFactory.id, FunctionMFactory.id))
    this
  }

  override def configure(): Unit = bind(classOf[LookupChildren]).toInstance(stub)
}