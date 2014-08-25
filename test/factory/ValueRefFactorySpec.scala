package factory

import com.google.inject.Injector
import composition.TestComposition
import models.common.IScope
import models.domain.scala.ValueRef
import modules.ai.legalGamer.LegalGamerModule
import org.mockito.Mockito._

final class ValueRefFactorySpec extends TestComposition {

  "create" must {
    "return instance of this type" in {
      val instance = factory.create(scope = scope)

      instance mustBe a[ValueRef]
    }

    "return expected given scope with 0 vals" in {
      val instance = factory.create(scope = scope)

      instance match {
        case ValueRef(name) => name must equal("v0")
      }
    }

    "return expected given scope with 1 val" in {
      val instance = factory.create(scope = scope)

      instance match {
        case ValueRef(name) => name must equal("v0")
        case _ => fail("wrong type")
      }
    }
  }

  "neighbours" must {
    "be empty" in {
      factory.neighbourIds.length must equal(0)
    }
  }

  "updateScope" must {
    "return unchanged" in {
      val result = factory.updateScope(scope)

      result must equal(scope)
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