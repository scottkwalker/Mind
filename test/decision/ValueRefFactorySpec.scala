package decision

import composition.DecisionBindings
import composition.StubCreateNodeBinding
import composition.StubCreateSeqNodesBinding
import composition.StubLookupChildrenWithFutures
import composition.StubSelectionStrategyBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.domain.scala.ValueRefImpl

final class ValueRefFactorySpec extends UnitTestHelpers with TestComposition {

  "create step" must {
    "return an instance of the expected type" in {
      val (valueRefFactory, scope) = build()

      val step = valueRefFactory.fillEmptySteps(scope = scope)

      whenReady(step) {
        _ mustBe a[ValueRefImpl]
      }(config = patienceConfig)
    }

    "return expected type and name if ai is stubbed to choose index zero" in {
      val (valueRefFactory, scope) = build()

      val step = valueRefFactory.fillEmptySteps(scope = scope)

      whenReady(step) {
        case ValueRefImpl(name) => name must equal("v0")
      }(config = patienceConfig)
    }

    "return expected type and name if ai is stubbed to choose index 1" in {
      val (valueRefFactory, scope) = build(chooseIndex = 1)

      val step = valueRefFactory.fillEmptySteps(scope = scope)

      whenReady(step) {
        case ValueRefImpl(name) => name must equal("v1")
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
    "throw an exception" in {
      val (valueRefFactory, scope) = build()
      a[RuntimeException] must be thrownBy valueRefFactory.createParams(scope).futureValue
    }
  }

  "createNodes" must {
    "throw an exception" in {
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