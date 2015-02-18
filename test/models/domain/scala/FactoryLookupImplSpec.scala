package models.domain.scala

import composition.DecisionBindings
import composition.FactoryLookupBinding
import composition.IntegerMBinding
import composition.StubCreateNodeBinding
import composition.StubCreateSeqNodesBinding
import composition.StubLookupChildrenWithFutures
import composition.TestComposition
import composition.UnitTestHelpers
import decision._
import utils.PozInt

final class FactoryLookupImplSpec extends UnitTestHelpers with TestComposition {

  "convert id to factory" must {
    "throw when id is not in the list" in {
      a[RuntimeException] must be thrownBy factoryLookup.convert(PozInt(Int.MaxValue))
    }

    "return expected factory" in {
      factoryLookup.convert(TypeTreeFactory.id) mustBe a[TypeTreeFactory]
    }
  }

  "convert factory to id" must {
    "throw when id is not in the list" in {
      a[RuntimeException] must be thrownBy factoryLookup.convert(mock[Decision])
    }

    "return expected id for AddOperatorFactory" in {
      val factory = factoryLookupInjector.getInstance(classOf[AddOperatorFactory])
      factoryLookup.convert(factory) must equal(AddOperatorFactory.id)
    }

    "return expected id for FunctionMFactory" in {
      val factory = factoryLookupInjector.getInstance(classOf[FunctionMFactory])
      factoryLookup.convert(factory) must equal(FunctionMFactory.id)
    }

    "return expected id for IntegerMFactory" in {
      val factory = factoryLookupInjector.getInstance(classOf[IntegerMFactory])
      factoryLookup.convert(factory) must equal(IntegerMFactory.id)
    }

    "return expected id for TypeTreeFactory" in {
      val factory = factoryLookupInjector.getInstance(classOf[TypeTreeFactory])
      factoryLookup.convert(factory) must equal(TypeTreeFactory.id)
    }

    "return expected id for objectFactory" in {
      val factory = factoryLookupInjector.getInstance(classOf[ObjectFactory])
      factoryLookup.convert(factory) must equal(ObjectFactory.id)
    }

    "return expected id for ValDclInFunctionParamFactory" in {
      val factory = factoryLookupInjector.getInstance(classOf[ValDclInFunctionParamFactory])
      factoryLookup.convert(factory) must equal(ValDclInFunctionParamFactory.id)
    }

    "return expected id for ValueRefFactory" in {
      val factory = factoryLookupInjector.getInstance(classOf[ValueRefFactory])
      factoryLookup.convert(factory) must equal(ValueRefFactory.id)
    }
  }

  private val factoryLookupInjector = testInjector(
    new FactoryLookupBinding,
    new DecisionBindings,
    new IntegerMBinding,
    new StubLookupChildrenWithFutures,
    new StubCreateNodeBinding,
    new StubCreateSeqNodesBinding
  )
  private val factoryLookup = factoryLookupInjector.getInstance(classOf[FactoryLookup])
}
