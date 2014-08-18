package nodes

import com.google.inject.Injector
import models.common.IScope
import models.domain.scala.ValueRef
import modules.ai.legalGamer.LegalGamerModule
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
      factory.neighbourIds.length should equal(0)
    }
  }

  "updateScope" should {
    "return unchanged" in {
      val result = factory.updateScope(scope)

      result should equal(scope)
    }
  }

  override lazy val injector: Injector = testInjector(new LegalGamerModule)
  private val factory = injector.getInstance(classOf[ValueRefFactoryImpl])
  private val scope = {
    val stub = mock[IScope]
    when(stub.numVals).thenReturn(1)
    stub
  }
}