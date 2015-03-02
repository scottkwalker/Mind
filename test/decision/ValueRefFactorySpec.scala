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
      val (valueRefFactory, scope) = build()

      val step = valueRefFactory.createStep(scope = scope)

      whenReady(step) {
        _ mustBe a[ValueRef]
      }(config = patienceConfig)
    }

    "return expected when ai stubbed to choose index zero" in {
      val (valueRefFactory, scope) = build()

      val step = valueRefFactory.createStep(scope = scope)

      whenReady(step) {
        case ValueRef(name) => name must equal("v0")
      }(config = patienceConfig)
    }

    "return expected when ai stubbed to choose index two" in {
      val (valueRefFactory, scope) = build(chooseIndex = 2)

      val step = valueRefFactory.createStep(scope = scope)

      whenReady(step) {
        case ValueRef(name) => name must equal("v2")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }
  }

  "neighbours" must {
    "be empty" in {
      val (valueRefFactory, _) = build()
      valueRefFactory.nodesToChooseFrom.size must equal(0)
    }
  }

  "updateScope" must {
    "return unchanged" in {
      val (valueRefFactory, scope) = build()

      val updateScope = valueRefFactory.updateScope(scope)

      updateScope must equal(scope)
    }
  }

  "createParams" must {
    "throw exception" in {
      val (valueRefFactory, scope) = build()
      a[RuntimeException] must be thrownBy valueRefFactory.createParams(scope).futureValue
    }
  }

  "createNodes" must {
    "throw exception" in {
      val (valueRefFactory, scope) = build()
      a[RuntimeException] must be thrownBy valueRefFactory.createNodes(scope).futureValue
    }
  }

  private def build(chooseIndex: Int = 0) = {
    val scope = mock[IScope]
    val valueRefFactory = testInjector(
      new DecisionBindings,
      new StubLookupChildrenWithFutures,
      new StubCreateNodeBinding,
      new StubCreateSeqNodesBinding,
      new StubSelectionStrategyBinding(chooseIndex = chooseIndex)
    ).getInstance(classOf[ValueRefFactory])
    (valueRefFactory, scope)
  }
}