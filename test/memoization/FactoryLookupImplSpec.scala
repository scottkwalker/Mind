package memoization

import composition.TestComposition
import replaceEmpty._

final class FactoryLookupImplSpec extends TestComposition {

  "convert id to factory" must {
    "throw when id is not in the list" in {
      a[RuntimeException] must be thrownBy sut.convert(-1)
    }

    "return expected factory" in {
      sut.convert(NodeTreeFactoryImpl.id) mustBe a [NodeTreeFactory]
    }
  }

  "convert factory to id" must {
    "throw when id is not in the list" in {
      a[RuntimeException] must be thrownBy sut.convert(mock[ReplaceEmpty])
    }

    "return expected id for AddOperatorFactory" in {
      val factory = injector.getInstance(classOf[AddOperatorFactory])
      sut.convert(factory) must equal(AddOperatorFactoryImpl.id)
    }

    "return expected id for FunctionMFactory" in {
      val factory = injector.getInstance(classOf[FunctionMFactory])
      sut.convert(factory) must equal(FunctionMFactoryImpl.id)
    }

    "return expected id for IntegerMFactory" in {
      val factory = injector.getInstance(classOf[IntegerMFactory])
      sut.convert(factory) must equal(IntegerMFactoryImpl.id)
    }

    "return expected id for NodeTreeFactory" in {
      val factory = injector.getInstance(classOf[NodeTreeFactory])
      sut.convert(factory) must equal(NodeTreeFactoryImpl.id)
    }

    "return expected id for ObjectDefFactory" in {
      val factory = injector.getInstance(classOf[ObjectDefFactory])
      sut.convert(factory) must equal(ObjectDefFactoryImpl.id)
    }

    "return expected id for ValDclInFunctionParamFactory" in {
      val factory = injector.getInstance(classOf[ValDclInFunctionParamFactory])
      sut.convert(factory) must equal(ValDclInFunctionParamFactoryImpl.id)
    }

    "return expected id for ValueRefFactory" in {
      val factory = injector.getInstance(classOf[ValueRefFactory])
      sut.convert(factory) must equal(ValueRefFactoryImpl.id)
    }
  }

  private val injector = testInjector()
  private val sut = injector.getInstance(classOf[FactoryLookup])
}
