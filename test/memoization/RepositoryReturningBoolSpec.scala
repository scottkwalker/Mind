package memoization

import java.util.concurrent.CountDownLatch

import composition.TestComposition
import memoization.RepositoryReturningBool.reads
import models.common.Scope
import models.domain.scala.FactoryLookup
import org.mockito.Mockito._
import play.api.libs.json._
import replaceEmpty.AddOperatorFactory
import replaceEmpty.ValueRefFactory
import replaceEmpty.ValueRefFactory
import utils.PozInt

final class RepositoryReturningBoolSpec extends TestComposition {

  "apply" must {
    "return true for ids that are valid for this scope" in {
      val (sut, _) = createSut()
      val a = sut.apply(key1 = scope, key2 = AddOperatorFactory.id)
      val b = sut.apply(key1 = scope, key2 = ValueRefFactory.id)

      a must equal(false)
      b must equal(true)
    }

    "only runs the function once for the same input" in {
      val (sut, factoryIdToFactory) = createSut()
      val a = sut.apply(key1 = scope, key2 = ValueRefFactory.id)
      val b = sut.apply(key1 = scope, key2 = ValueRefFactory.id)
      val c = sut.apply(key1 = scope, key2 = AddOperatorFactory.id)
      val d = sut.apply(key1 = scope, key2 = AddOperatorFactory.id)

      a must equal(true)
      b must equal(true)
      c must equal(false)
      d must equal(false)
      verify(factoryIdToFactory, times(1)).convert(AddOperatorFactory.id)
      verify(factoryIdToFactory, times(1)).convert(ValueRefFactory.id)
    }
  }

  "write macro inception" must {
    "write expected json for no computed values" in {
      val cache = Map[String, Either[CountDownLatch, Boolean]]()
      RepositoryReturningBool.writes.writes(cache) must equal(
        JsObject(
          Seq.empty
        )
      )
    }

    "write expected json for one computed value" in {
      val cache = Map(
        s"Scope(0,0,0,1,0,0,0,0,1)|${ValueRefFactory.id}" -> Right(true)
      )
      RepositoryReturningBool.writes.writes(cache) must equal(
        JsObject(
          Seq(
            (s"Scope(0,0,0,1,0,0,0,0,1)|${ValueRefFactory.id}", JsBoolean(value = true))
          )
        )
      )
    }

    "write expected json for many computed values" in {
      val cache = Map(
        s"Scope(0,0,0,1,0,0,0,0)|${AddOperatorFactory.id}" -> Right(false),
        s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactory.id}" -> Right(false),
        s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactory.id}" -> Right(true)
      )
      RepositoryReturningBool.writes.writes(cache) must equal(
        JsObject(
          Seq(
            (s"Scope(0,0,0,1,0,0,0,0)|${AddOperatorFactory.id}", JsBoolean(value = false)),
            (s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactory.id}", JsBoolean(value = false)),
            (s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactory.id}", JsBoolean(value = true))
          )
        )
      )
    }

    "ignore values that have not finished computation" in {
      val cache = Map(
        s"Scope(0,0,0,1,0,0,0,0)|${AddOperatorFactory.id}" -> Left(new CountDownLatch(0)), // This value is still being computed so should not appear in the output.
        s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactory.id}" -> Right(false),
        s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactory.id}" -> Right(true)
      )
      RepositoryReturningBool.writes.writes(cache) must equal(
        JsObject(
          Seq(
            (s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactory.id}", JsBoolean(value = false)),
            (s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactory.id}", JsBoolean(value = true))
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
      val result = sut.apply(key1 = scope, key2 = ValueRefFactory.id)
      result must equal(true) // Check the calculated value is correct before continuing to test it is written correctly.
      sut.write must equal(
        JsObject(
          Seq(
            "versioning" -> JsString(version),
            "cache" -> JsObject(
              Seq(
                (s"Scope(0,0,0,1,0,0,0,0,1)|${ValueRefFactory.id}", JsBoolean(value = true))
              )
            )
          )
        )
      )
    }

    "write expected json for many computed values" in {
      val (sut, _) = createSut()
      sut.apply(key1 = scope, key2 = AddOperatorFactory.id)
      sut.apply(key1 = scope, key2 = ValueRefFactory.id)

      sut.write must equal(
        JsObject(
          Seq(
            "versioning" -> JsString(version),
            "cache" -> JsObject(
              Seq(
                (s"Scope(0,0,0,1,0,0,0,0,1)|${AddOperatorFactory.id}", JsBoolean(value = false)),
                (s"Scope(0,0,0,0,0,0,0,0,1)|${ValueRefFactory.id}", JsBoolean(value = false)),
                (s"Scope(0,0,0,1,0,0,0,0,1)|${ValueRefFactory.id}", JsBoolean(value = true))
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
              (s"Scope(0,0,0,0,0,0,0,1,0)|${AddOperatorFactory.id}", JsBoolean(value = false)),
              (s"Scope(0,0,0,1,0,0,0,1,0)|${ValueRefFactory.id}", JsBoolean(value = false)),
              (s"Scope(0,0,0,0,0,0,0,1,0)|${ValueRefFactory.id}", JsBoolean(value = true))
            )
          )
        )
      )
      val readsFromJson = reads(factoryLookupStub)
      val asObj: RepositoryReturningBool = Memoize2Impl.read[RepositoryReturningBool](json)(readsFromJson)
      val a = asObj.apply(key1 = scope, key2 = AddOperatorFactory.id)
      val b = asObj.apply(key1 = scope, key2 = ValueRefFactory.id)

      a must equal(false)
      b must equal(true)
    }

    "throw RuntimeException when versioning string doesn't match what we intend to use" in {
      val versioningWithoutAddOp = s"${ValueRefFactory.id}"
      val json = JsObject(
        Seq(
          "versioning" -> JsString(versioningWithoutAddOp),
          "cache" -> JsObject(
            Seq(
              (s"Scope(0,0,0,0,0,0,0,1,0)|${AddOperatorFactory.id}", JsBoolean(value = false)),
              (s"Scope(0,0,0,1,0,0,0,1,0)|${ValueRefFactory.id}", JsBoolean(value = false)),
              (s"Scope(0,0,0,0,0,0,0,1,0)|${ValueRefFactory.id}", JsBoolean(value = true))
            )
          )
        )
      )
      val readsFromJson = reads(factoryLookupStub)

      a[RuntimeException] must be thrownBy Memoize2Impl.read[RepositoryReturningBool](json)(readsFromJson)
    }
  }

  "size" must {
    "return 0 when cache empty" in pending
    "return 1 when cache contains 1 item" in pending
    "return 3 when cache contains 3 items" in pending
  }

  private val scope = Scope(height = 1, maxHeight = 1)

  private def createSut() = {
    val factoryLookup = factoryLookupStub
    val sut = new RepositoryReturningBool(factoryLookup = factoryLookup)
    (sut, factoryLookup)
  }

  private val version = s"${AddOperatorFactory.id}|${ValueRefFactory.id}"

  private def factoryLookupStub = {
    val addOperatorFactory = {
      val stub = mock[AddOperatorFactory]
      when(stub.nodesToChooseFrom).thenReturn(Set(ValueRefFactory.id))
      stub
    }
    val valueRefFactoryImpl = {
      val stub = mock[ValueRefFactory]
      when(stub.nodesToChooseFrom).thenReturn(Set.empty[PozInt])
      stub
    }

    val stub = mock[FactoryLookup]
    when(stub.version).thenReturn(version)
    when(stub.convert(AddOperatorFactory.id)).thenReturn(addOperatorFactory)
    when(stub.convert(ValueRefFactory.id)).thenReturn(valueRefFactoryImpl)
    stub
  }
}