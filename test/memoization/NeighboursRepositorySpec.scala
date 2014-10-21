package memoization

import java.util.concurrent.CountDownLatch
import composition.TestComposition
import replaceEmpty._
import memoization.NeighboursRepository.readsNeighboursRepository
import models.common.Scope
import org.mockito.Mockito._
import play.api.libs.json._

class NeighboursRepositorySpec extends TestComposition {

  "apply" must {
    "return true for ids that are valid for this scope" in {
      val (sut, _) = createSut()

      sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id) must equal(false)
      sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id) must equal(true)
    }

    "only runs the function once for the same input" in {
      val (sut, factoryIdToFactory) = createSut()
      sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id) must equal(true)
      sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id) must equal(true)
      sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id) must equal(false)
      sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id) must equal(false)

      verify(factoryIdToFactory, times(1)).convert(AddOperatorFactoryImpl.id)
      verify(factoryIdToFactory, times(1)).convert(ValueRefFactoryImpl.id)
    }
  }

  "write" must {
    "write expected json for no computed values" in {
      val (sut, _) = createSut()

      sut.write must equal(
        JsObject(
          Seq(
            "versioning" -> JsString(version),
            "cache" -> JsObject(
              Seq.empty
            )
          )
        )
      )
    }

    "write expected json for one computed value" in {
      val (sut, _) = createSut()
      sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id) must equal(true)

      sut.write must equal(
        JsObject(
          Seq(
            "versioning" -> JsString(version),
            "cache" -> JsObject(
              Seq(
                (s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = true))
              )
            )
          )
        )
      )
    }

    "write expected json for many computed values" in {
      val (sut, _) = createSut()
      sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id) must equal(false)
      sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id) must equal(true)

      sut.write must equal(
        JsObject(
          Seq(
            "versioning" -> JsString(version),
            "cache" -> JsObject(
              Seq(
                (s"Scope(0,0,0,1,0,0,0,0)|${AddOperatorFactoryImpl.id}", JsBoolean(value = false)),
                (s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = false)),
                (s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = true))
              )
            )
          )
        )
      )
    }
  }

  "read" must {
    "convert from json to usable object" in {
      val json = JsObject(
        Seq(
          "versioning" -> JsString(version),
          "cache" -> JsObject(
            Seq(
              (s"Scope(0,0,0,0,0,0,0,1,0)|${AddOperatorFactoryImpl.id}", JsBoolean(value = false)),
              (s"Scope(0,0,0,1,0,0,0,1,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = false)),
              (s"Scope(0,0,0,0,0,0,0,1,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = true))
            )
          )
        )
      )
      val readsFromJson = readsNeighboursRepository(factoryIdToFactoryStub)
      val asObj: NeighboursRepository = Memoize2Impl.read[NeighboursRepository](json)(readsFromJson)

      asObj.apply(scope, AddOperatorFactoryImpl.id) must equal(false)
      asObj.apply(scope, ValueRefFactoryImpl.id) must equal(true)
    }

    "throw RuntimeException when versioning string doesn't match what we intend to use" in {
      val versioningWithoutAddOp = s"${ValueRefFactoryImpl.id}"
      val json = JsObject(
        Seq(
          "versioning" -> JsString(versioningWithoutAddOp),
          "cache" -> JsObject(
            Seq(
              (s"Scope(0,0,0,0,0,0,0,1,0)|${AddOperatorFactoryImpl.id}", JsBoolean(value = false)),
              (s"Scope(0,0,0,1,0,0,0,1,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = false)),
              (s"Scope(0,0,0,0,0,0,0,1,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = true))
            )
          )
        )
      )
      val readsFromJson = readsNeighboursRepository(factoryIdToFactoryStub)

      a[RuntimeException] must be thrownBy Memoize2Impl.read[NeighboursRepository](json)(readsFromJson)
    }
  }

  private val scope = Scope(height = 1)
  private val injector = testInjector()
  private val addOperatorFactoryImpl = injector.getInstance(classOf[AddOperatorFactoryImpl])
  private val valueRefFactoryImpl = injector.getInstance(classOf[ValueRefFactoryImpl])
  private val version = s"${AddOperatorFactoryImpl.id}|${ValueRefFactoryImpl.id}"

  private def createSut() = {
    val factoryIdToFactory = factoryIdToFactoryStub
    val sut = new NeighboursRepository(factoryLookup = factoryIdToFactory)
    (sut, factoryIdToFactory)
  }

  private def factoryIdToFactoryStub = {
    val stub = mock[FactoryLookup]
    when(stub.version).thenReturn(version)
    when(stub.convert(AddOperatorFactoryImpl.id)).thenReturn(addOperatorFactoryImpl)
    when(stub.convert(ValueRefFactoryImpl.id)).thenReturn(valueRefFactoryImpl)
    stub
  }
}