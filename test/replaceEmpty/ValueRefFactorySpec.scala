package replaceEmpty

import composition.TestComposition
import models.common.IScope
import models.domain.scala.ValueRef
import org.mockito.Mockito._

final class ValueRefFactorySpec extends TestComposition {

  "create" must {
    "return instance of this type" in {
      val instruction = valueRefFactory.create(scope = scope)

      whenReady(instruction) { result =>
        result mustBe a[ValueRef]
      }
    }

    "return expected given scope with 0 vals" in {
      val instruction = valueRefFactory.create(scope = scope)

      whenReady(instruction) {
        case ValueRef(name) => name must equal("v0")
      }
    }

    "return expected given scope with 1 val" in {
      val instruction = valueRefFactory.create(scope = scope)

      whenReady(instruction) {
        case ValueRef(name) => name must equal("v0")
        case _ => fail("wrong type")
      }
    }
  }

  "neighbours" must {
    "be empty" in {
      valueRefFactory.nodesToChooseFrom.size must equal(0)
    }
  }

  "updateScope" must {
    "return unchanged" in {
      val result = valueRefFactory.updateScope(scope)

      result must equal(scope)
    }
  }

  private def valueRefFactory = testInjector().getInstance(classOf[ValueRefFactoryImpl])
  private val scope = {
    val stub = mock[IScope]
    when(stub.numVals).thenReturn(1)
    stub
  }
}