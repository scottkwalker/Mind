package decision

import composition.DecisionBindings
import composition.StubCreateNodeBinding
import composition.StubCreateSeqNodesBinding
import composition.StubLookupChildrenWithFutures
import composition.StubSelectionStrategyBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.domain.scala.ValueRef
import org.mockito.Mockito._

import scala.concurrent.Await

final class ValueRefFactorySpec extends UnitTestHelpers with TestComposition {

  "create step" must {
    "return instance of this type" in {
      val step = valueRefFactory.createStep(scope = scope)

      whenReady(step) {
        _ mustBe a[ValueRef]
      }(config = patienceConfig)
    }

    "return expected given scope with 0 vals" in {
      val step = valueRefFactory.createStep(scope = scope)

      whenReady(step) {
        case ValueRef(name) => name must equal("v0")
      }(config = patienceConfig)
    }

    "return expected given scope with 1 val" in {
      val step = valueRefFactory.createStep(scope = scope)

      whenReady(step) {
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
      val updateScope = valueRefFactory.updateScope(scope)

      updateScope must equal(scope)
    }
  }

  "createParams" must {
    "throw exception" in {
      val scope = mock[IScope]

      a[RuntimeException] must be thrownBy valueRefFactory.createParams(scope).futureValue
    }
  }

  "createNodes" must {
    "throw exception" in {
      val scope = mock[IScope]

      a[RuntimeException] must be thrownBy valueRefFactory.createNodes(scope).futureValue
    }
  }

  private val scope = {
    val stub = mock[IScope]
    when(stub.numVals).thenReturn(1)
    stub
  }

  private def valueRefFactory = testInjector(
    new DecisionBindings,
    new StubLookupChildrenWithFutures,
    new StubCreateNodeBinding,
    new StubCreateSeqNodesBinding,
    new StubSelectionStrategyBinding
  ).getInstance(classOf[ValueRefFactory])
}