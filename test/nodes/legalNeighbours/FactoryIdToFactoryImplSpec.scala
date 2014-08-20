package nodes.legalNeighbours

import nodes._
import utils.helpers.UnitSpec

final class FactoryIdToFactoryImplSpec extends UnitSpec {

  "convert id to factory" should {
    "throw when id is not in the list" in {
      a[RuntimeException] should be thrownBy sut.convert(-1)
    }

    "return expected factory" in {
      factoryIdToFactory.convert(NodeTreeFactoryImpl.id) should equal(nodeTreeFactory)
    }
  }

  "convert factory to id" should {
    "throw when id is not in the list" in {
      a[RuntimeException] should be thrownBy sut.convert(-1)
    }

    "return expected id" in {
      val nodeTreeFactory = injector.getInstance(classOf[NodeTreeFactory])
      sut.convert(nodeTreeFactory) should equal(NodeTreeFactoryImpl.id)
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
  private val sut = injector.getInstance(classOf[FactoryIdToFactory])
}
