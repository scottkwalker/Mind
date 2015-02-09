package decision

import composition.TestComposition
import models.common.IScope
import models.domain.scala.ValueRef
import org.mockito.Mockito._

final class ValueRefFactorySpec extends TestComposition {

  "create step" must {
    "return instance of this type" in {
      val instruction = valueRefFactory.createStep(scope = scope)

      whenReady(instruction) { result =>
        result mustBe a[ValueRef]
      }(config = patienceConfig)
    }

    "return expected given scope with 0 vals" in {
      val instruction = valueRefFactory.createStep(scope = scope)

      whenReady(instruction) {
        case ValueRef(name) => name must equal("v0")
      }(config = patienceConfig)
    }

    "return expected given scope with 1 val" in {
      val instruction = valueRefFactory.createStep(scope = scope)

      whenReady(instruction) {
        case ValueRef(name) => name must equal("v0")
        case _ => fail("wrong type")
      }(config = patienceConfig)
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