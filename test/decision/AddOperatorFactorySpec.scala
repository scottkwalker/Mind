package decision

import composition.DecisionBindings
import composition.StubCreateNodeBinding
import composition.StubCreateSeqNodesBinding
import composition.StubLookupChildrenWithFutures
import composition.StubSelectionStrategyBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.domain.scala.AddOperatorImpl

final class AddOperatorFactorySpec extends UnitTestHelpers with TestComposition {

  "create step" must {
    "return an instance of the expected type" in {
      val (addOperatorFactory, scope) = build

      val step = addOperatorFactory.createStep(scope = scope)

      whenReady(step) {
        _ mustBe a[AddOperatorImpl]
      }(config = patienceConfig)
    }
  }

  "createParams" must {
    "throw an exception" in {
      val (addOperatorFactory, scope) = build
      a[RuntimeException] must be thrownBy addOperatorFactory.createParams(scope).futureValue
    }
  }

  "createNodes" must {
    "throw an exception" in {
      val (addOperatorFactory, scope) = build
      a[RuntimeException] must be thrownBy addOperatorFactory.createNodes(scope).futureValue
    }
  }

  private def build = {
    val scope = mock[IScope]
    val addOperatorFactory = testInjector(
      new DecisionBindings,
      new StubLookupChildrenWithFutures,
      new StubCreateNodeBinding,
      new StubCreateSeqNodesBinding,
      new StubSelectionStrategyBinding
    ).getInstance(classOf[AddOperatorFactory])
    (addOperatorFactory, scope)
  }
}