package replaceEmpty

import composition.TestComposition
import models.common.IScope
import models.domain.scala.ValueRef
import org.mockito.Mockito._

final class ValueRefFactorySpec extends TestComposition {

  "create" must {
    "return instance of this type" in {
      val instance = factory.create(scope = scope)

      whenReady(instance) { result =>
        result mustBe a[ValueRef]
      }
    }

    "return expected given scope with 0 vals" in {
      val instance = factory.create(scope = scope)

      whenReady(instance) {
        case ValueRef(name) => name must equal("v0")
      }
    }

    "return expected given scope with 1 val" in {
      val instance = factory.create(scope = scope)

      whenReady(instance) {
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

  private val factory = testInjector().getInstance(classOf[ValueRefFactoryImpl])
  private val scope = {
    val stub = mock[IScope]
    when(stub.numVals).thenReturn(1)
    stub
  }
}