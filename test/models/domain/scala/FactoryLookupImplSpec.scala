package models.domain.scala

import composition.FactoryLookupBinding
import composition.TestComposition
import replaceEmpty._
import utils.PozInt

final class FactoryLookupImplSpec extends TestComposition {

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
      a[RuntimeException] must be thrownBy factoryLookup.convert(mock[ReplaceEmpty])
    }

    "return expected id for AddOperatorFactory" in {
      val factory = injector.getInstance(classOf[AddOperatorFactory])
      factoryLookup.convert(factory) must equal(AddOperatorFactory.id)
    }

    "return expected id for FunctionMFactory" in {
      val factory = injector.getInstance(classOf[FunctionMFactory])
      factoryLookup.convert(factory) must equal(FunctionMFactory.id)
    }

    "return expected id for IntegerMFactory" in {
      val factory = injector.getInstance(classOf[IntegerMFactory])
      factoryLookup.convert(factory) must equal(IntegerMFactory.id)
    }

    "return expected id for TypeTreeFactory" in {
      val factory = injector.getInstance(classOf[TypeTreeFactory])
      factoryLookup.convert(factory) must equal(TypeTreeFactory.id)
    }

    "return expected id for objectFactory" in {
      val factory = injector.getInstance(classOf[ObjectFactory])
      factoryLookup.convert(factory) must equal(ObjectFactory.id)
    }

    "return expected id for ValDclInFunctionParamFactory" in {
      val factory = injector.getInstance(classOf[ValDclInFunctionParamFactory])
      factoryLookup.convert(factory) must equal(ValDclInFunctionParamFactory.id)
    }

    "return expected id for ValueRefFactory" in {
      val factory = injector.getInstance(classOf[ValueRefFactory])
      factoryLookup.convert(factory) must equal(ValueRefFactory.id)
    }
  }

  private val injector = testInjector(new FactoryLookupBinding)
  private val factoryLookup = injector.getInstance(classOf[FactoryLookup])
}
