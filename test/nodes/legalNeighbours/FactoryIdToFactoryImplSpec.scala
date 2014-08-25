package nodes.legalNeighbours

import nodes._
import nodes.helpers.ICreateChildNodes
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
      a[RuntimeException] should be thrownBy sut.convert(mock[ICreateChildNodes])
    }

    "return expected id for AddOperatorFactory" in {
      val factory = injector.getInstance(classOf[AddOperatorFactory])
      sut.convert(factory) should equal(AddOperatorFactoryImpl.id)
    }

    "return expected id for FunctionMFactory" in {
      val factory = injector.getInstance(classOf[FunctionMFactory])
      sut.convert(factory) should equal(FunctionMFactoryImpl.id)
    }

    "return expected id for IntegerMFactory" in {
      val factory = injector.getInstance(classOf[IntegerMFactory])
      sut.convert(factory) should equal(IntegerMFactoryImpl.id)
    }

    "return expected id for NodeTreeFactory" in {
      val factory = injector.getInstance(classOf[NodeTreeFactory])
      sut.convert(factory) should equal(NodeTreeFactoryImpl.id)
    }

    "return expected id for ObjectDefFactory" in {
      val factory = injector.getInstance(classOf[ObjectDefFactory])
      sut.convert(factory) should equal(ObjectDefFactoryImpl.id)
    }

    "return expected id for ValDclInFunctionParamFactory" in {
      val factory = injector.getInstance(classOf[ValDclInFunctionParamFactory])
      sut.convert(factory) should equal(ValDclInFunctionParamFactoryImpl.id)
    }

    "return expected id for ValueRefFactory" in {
      val factory = injector.getInstance(classOf[ValueRefFactory])
      sut.convert(factory) should equal(ValueRefFactoryImpl.id)
    }
  }

  private val nodeTreeFactory = mock[NodeTreeFactory]
  private val factoryIdToFactory = {
    val addOperatorFactory = mock[AddOperatorFactory]
    val functionMFactory = mock[FunctionMFactory]
    val integerMFactory = mock[IntegerMFactory]
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
