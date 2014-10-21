package memoization

import java.util.concurrent.CountDownLatch
import composition.TestComposition
import memoization.NeighboursRepository.readsNeighboursRepository
import models.common.Scope
import org.mockito.Mockito._
import play.api.libs.json._
import replaceEmpty._

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

  "write macro inception" must {
    "write expected json for no computed values" in {
      val cache = Map[String, Either[CountDownLatch, Boolean]]()
      NeighboursRepository.writesNeighboursRepository.writes(cache) must equal(
        JsObject(
          Seq.empty
        )
      )
    }

    "write expected json for one computed value" in {
      val cache = Map(
        s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactoryImpl.id}" -> Right(true)
      )
      NeighboursRepository.writesNeighboursRepository.writes(cache) must equal(
        JsObject(
          Seq(
            (s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = true))
          )
        )
      )
    }

    "write expected json for many computed values" in {
      val cache = Map(
        s"Scope(0,0,0,1,0,0,0,0)|${AddOperatorFactoryImpl.id}" -> Right(false),
        s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactoryImpl.id}" -> Right(false),
        s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactoryImpl.id}" -> Right(true)
      )
      NeighboursRepository.writesNeighboursRepository.writes(cache) must equal(
        JsObject(
          Seq(
            (s"Scope(0,0,0,1,0,0,0,0)|${AddOperatorFactoryImpl.id}", JsBoolean(value = false)),
            (s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = false)),
            (s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = true))
          )
        )
      )
    }

    "ignore values that have not finished computation" in {
      val cache = Map(
        s"Scope(0,0,0,1,0,0,0,0)|${AddOperatorFactoryImpl.id}" -> Left(new CountDownLatch(0)), // This value is still being computed so should not appear in the output.
        s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactoryImpl.id}" -> Right(false),
        s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactoryImpl.id}" -> Right(true)
      )
      NeighboursRepository.writesNeighboursRepository.writes(cache) must equal(
        JsObject(
          Seq(
            (s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = false)),
            (s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactoryImpl.id}", JsBoolean(value = true))
          )
        )
      )
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
      val readsFromJson = readsNeighboursRepository(factoryLookupStub)
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
      val readsFromJson = readsNeighboursRepository(factoryLookupStub)

      a[RuntimeException] must be thrownBy Memoize2Impl.read[NeighboursRepository](json)(readsFromJson)
    }
  }

  private val scope = Scope(height = 1)

  private def createSut() = {
    val factoryLookup = factoryLookupStub
    val sut = new NeighboursRepository(factoryLookup = factoryLookup)
    (sut, factoryLookup)
  }

  private val version = s"${AddOperatorFactoryImpl.id}|${ValueRefFactoryImpl.id}"

  private def factoryLookupStub = {
    val addOperatorFactoryImpl = {
      val stub = mock[AddOperatorFactory]
      when(stub.neighbourIds).thenReturn(Seq(ValueRefFactoryImpl.id))
      stub
    }
    val valueRefFactoryImpl = {
      val stub = mock[ValueRefFactory]
      when(stub.neighbourIds).thenReturn(Seq.empty)
      stub
    }

    val stub = mock[FactoryLookup]
    when(stub.version).thenReturn(version)
    when(stub.convert(AddOperatorFactoryImpl.id)).thenReturn(addOperatorFactoryImpl)
    when(stub.convert(ValueRefFactoryImpl.id)).thenReturn(valueRefFactoryImpl)
    stub
  }
}