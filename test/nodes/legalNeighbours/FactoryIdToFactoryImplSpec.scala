package nodes.legalNeighbours

import nodes._
import utils.helpers.UnitSpec

final class FactoryIdToFactoryImplSpec extends UnitSpec {

  "convert" should {
    "throw when id is not in the list" in {
      a[RuntimeException] should be thrownBy injector.getInstance(classOf[FactoryIdToFactory]).convert(-1)
    }

    "return expected factory" in {
      factoryIdToFactory.convert(NodeTreeFactoryImpl.id) should equal(nodeTreeFactory)
    }
  }

  private val nodeTreeFactory = mock[NodeTreeFactory]
  private val factoryIdToFactory = {
    val addOperatorFactory = mock[AddOperatorFactory]
    val functionMFactory = mock[FunctionMFactory]
    val integerMFactory = mock[IntegerMFactoryImpl]
    val objectDefFactory = mock[ObjectDefFactory]
    val valDclInFunctionParamFactory = mock[ValDclInFunctionParamFactory]
    val valueRefFactory = mock[ValueRefFactory]
    new FactoryIdToFactoryImpl(
      addOperatorFactory,
      functionMFactory,
      integerMFactory,
      nodeTreeFactory,
      objectDefFactory,
      valDclInFunctionParamFactory,
      valueRefFactory
    )
  }
}
