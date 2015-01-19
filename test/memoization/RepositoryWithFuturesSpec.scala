package memoization

import java.util.concurrent.CountDownLatch

import composition.TestComposition
import memoization.RepositoryWithFutures.readsNeighboursRepository
import models.common.Scope
import models.domain.scala.FactoryLookup
import org.mockito.Mockito._
import play.api.libs.json._
import replaceEmpty.{AddOperatorFactory, AddOperatorFactoryImpl, ValueRefFactory, ValueRefFactoryImpl}
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RepositoryWithFuturesSpec extends TestComposition {

  "apply" must {
    "return true for ids that are valid for this scope" in {
      val (sut, _) = createSut()
      val a = sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id)
      val b = sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id)

      whenReady(Future.sequence(Seq(a, b)), browserTimeout) { r =>
        r(0) must equal(false)
        r(1) must equal(true)
      }
    }

    "only runs the function once for the same input" in {
      val (sut, factoryIdToFactory) = createSut()
      val a = sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id)
      val b = sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id)
      val c = sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id)
      val d = sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id)

      whenReady(Future.sequence(Seq(a, b, c, d)), browserTimeout) { r =>
        r(0) must equal(true)
        r(1) must equal(true)
        r(2) must equal(false)
        r(3) must equal(false)
        verify(factoryIdToFactory, times(1)).convert(AddOperatorFactoryImpl.id)
        verify(factoryIdToFactory, times(1)).convert(ValueRefFactoryImpl.id)
      }
    }
  }

  "write macro inception" must {
    "write expected json for no computed values" in {
      val cache = Map[String, Either[CountDownLatch, Future[Boolean]]]()
      RepositoryWithFutures.writesNeighboursRepository.writes(cache) must equal(
        JsObject(
          Seq.empty
        )
      )
    }

    "write expected json for one computed value" in {
      val cache = Map(
        s"Scope(0,0,0,1,0,0,0,0,1)|${ValueRefFactoryImpl.id}" -> Right(Future.successful(true))
      )
      RepositoryWithFutures.writesNeighboursRepository.writes(cache) must equal(
        JsObject(
          Seq(
            (s"Scope(0,0,0,1,0,0,0,0,1)|${ValueRefFactoryImpl.id}", JsBoolean(value = true))
          )
        )
      )
    }

    "write expected json for many computed values" in {
      val cache = Map(
        s"Scope(0,0,0,1,0,0,0,0)|${AddOperatorFactoryImpl.id}" -> Right(Future.successful(false)),
        s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactoryImpl.id}" -> Right(Future.successful(false)),
        s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactoryImpl.id}" -> Right(Future.successful(true))
      )
      RepositoryWithFutures.writesNeighboursRepository.writes(cache) must equal(
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
        s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactoryImpl.id}" -> Right(Future.successful(false)),
        s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactoryImpl.id}" -> Right(Future.successful(true))
      )
      RepositoryWithFutures.writesNeighboursRepository.writes(cache) must equal(
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
      whenReady(sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id), browserTimeout) { r =>
        r must equal(true) // Check the calculated value is correct before continuing to test it is written correctly.
        sut.write must equal(
          JsObject(
            Seq(
              "versioning" -> JsString(version),
              "cache" -> JsObject(
                Seq(
                  (s"Scope(0,0,0,1,0,0,0,0,1)|${ValueRefFactoryImpl.id}", JsBoolean(value = true))
                )
              )
            )
          )
        )
      }
    }

    "write expected json for many computed values" in {
      val (sut, _) = createSut()
      val a = sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id)
      val b = sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id)

      whenReady(Future.sequence(Seq(a, b)), browserTimeout) { r =>
        sut.write must equal(
          JsObject(
            Seq(
              "versioning" -> JsString(version),
              "cache" -> JsObject(
                Seq(
                  (s"Scope(0,0,0,1,0,0,0,0,1)|${AddOperatorFactoryImpl.id}", JsBoolean(value = false)),
                  (s"Scope(0,0,0,0,0,0,0,0,1)|${ValueRefFactoryImpl.id}", JsBoolean(value = false)),
                  (s"Scope(0,0,0,1,0,0,0,0,1)|${ValueRefFactoryImpl.id}", JsBoolean(value = true))
                )
              )
            )
          )
        )
      }
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
      val asObj: RepositoryWithFutures = Memoize2Impl.read[RepositoryWithFutures](json)(readsFromJson)
      val a = asObj.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id)
      val b = asObj.apply(key1 = scope, key2 = ValueRefFactoryImpl.id)

      whenReady(Future.sequence(Seq(a, b)), browserTimeout) { r =>
        r(0) must equal(false)
        r(1) must equal(true)
      }
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

      a[RuntimeException] must be thrownBy Memoize2Impl.read[RepositoryWithFutures](json)(readsFromJson)
    }
  }

  private val scope = Scope(height = 1, maxHeight = 1)

  private def createSut() = {
    val factoryLookup = factoryLookupStub
    val sut = new RepositoryWithFutures(factoryLookup = factoryLookup)
    (sut, factoryLookup)
  }

  private val version = s"${AddOperatorFactoryImpl.id}|${ValueRefFactoryImpl.id}"

  private def factoryLookupStub = {
    val addOperatorFactoryImpl = {
      val stub = mock[AddOperatorFactory]
      when(stub.nodesToChooseFrom).thenReturn(Set(ValueRefFactoryImpl.id))
      stub
    }
    val valueRefFactoryImpl = {
      val stub = mock[ValueRefFactory]
      when(stub.nodesToChooseFrom).thenReturn(Set.empty[PozInt])
      stub
    }

    val stub = mock[FactoryLookup]
    when(stub.version).thenReturn(version)
    when(stub.convert(AddOperatorFactoryImpl.id)).thenReturn(addOperatorFactoryImpl)
    when(stub.convert(ValueRefFactoryImpl.id)).thenReturn(valueRefFactoryImpl)
    stub
  }
}