package nodes

import nodes.helpers.IScope
import com.google.inject.Injector
import com.google.inject.Guice
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import models.domain.scala.ValueRef
import org.mockito.Mockito._
import utils.helpers.UnitSpec

final class ValueRefFactorySpec extends UnitSpec {
  "create" should {
    "return instance of this type" in {
      val instance = factory.create(scope = scope)

      instance shouldBe a[ValueRef]
    }

    "return expected given scope with 0 vals" in {
      val instance = factory.create(scope = scope)

      instance match {
        case ValueRef(name) => name should equal("v0")
      }
    }

    "return expected given scope with 1 val" in {
      val instance = factory.create(scope = scope)

      instance match {
        case ValueRef(name) => name should equal("v0")
        case _ => fail("wrong type")
      }
    }
  }

  "neighbours" should {
    "be empty" in {
      factory.neighbours.length should equal(0)
    }
  }

  "updateScope" should {
    "return unchanged" in {
      val result = factory.updateScope(scope)

      result should equal(scope)
    }
  }

  private val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
  private val factory = injector.getInstance(classOf[ValueRefFactory])
  private val scope = mock[IScope]
  when(scope.numVals).thenReturn(1)
}