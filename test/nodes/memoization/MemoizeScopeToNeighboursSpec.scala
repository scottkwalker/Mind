package nodes.memoization

import com.google.inject.{Guice, Injector}
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import nodes.helpers.Scope
import nodes.legalNeighbours.FactoryIdToFactory
import nodes.memoization.MemoizeScopeToNeighbours.readsMemoizeScopeToNeighbours
import nodes.{AddOperatorFactoryImpl, ValueRefFactoryImpl}
import org.mockito.Mockito._
import play.api.libs.json._
import utils.helpers.UnitSpec

class MemoizeScopeToNeighboursSpec extends UnitSpec {

  "apply" should {
    "return true for ids that are valid for this scope" in {
      val (sut, _) = createSut

      sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id) should equal(false)
      sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id) should equal(true)
    }

    "only runs the function once for the same input" in {
      val (sut, factoryIdToFactory) = createSut
      sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id) should equal(true)
      sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id) should equal(true)
      sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id) should equal(false)
      sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id) should equal(false)

      verify(factoryIdToFactory, times(1)).convert(AddOperatorFactoryImpl.id)
      verify(factoryIdToFactory, times(1)).convert(ValueRefFactoryImpl.id)
    }
  }

  "write" should {
    "return the expected json" in {
      val (sut, _) = createSut
      sut.apply(key1 = scope, key2 = AddOperatorFactoryImpl.id) should equal(false)
      sut.apply(key1 = scope, key2 = ValueRefFactoryImpl.id) should equal(true)

      sut.write should equal(
        JsObject(
          Seq(
            "versioning" -> JsString("test"),
            "cache" -> JsObject(
              Seq(
                (s"Scope(0,0,0,1,0,0,0,0)|${AddOperatorFactoryImpl.id}", JsBoolean(false)),
                (s"Scope(0,0,0,0,0,0,0,0)|${ValueRefFactoryImpl.id}", JsBoolean(false)),
                (s"Scope(0,0,0,1,0,0,0,0)|${ValueRefFactoryImpl.id}", JsBoolean(true))
              )
            )
          )
        )
      )
    }
  }

  "read" should {

    "convert from json to usable object" in {
      val versioning = s"${AddOperatorFactoryImpl.id}|${ValueRefFactoryImpl.id}"
      val json = JsObject(
        Seq(
          "versioning" -> JsString(versioning),
          "cache" -> JsObject(
            Seq(
              (s"Scope(0,0,0,0,0,0,0,1,0)|${AddOperatorFactoryImpl.id}", JsBoolean(false)),
              (s"Scope(0,0,0,1,0,0,0,1,0)|${ValueRefFactoryImpl.id}", JsBoolean(false)),
              (s"Scope(0,0,0,0,0,0,0,1,0)|${ValueRefFactoryImpl.id}", JsBoolean(true))
            )
          )
        )
      )

      val readsFromJson = readsMemoizeScopeToNeighbours(versioning)(factoryIdToFactoryStub)
      val asObj: MemoizeScopeToNeighbours = Memoize2Impl.read[MemoizeScopeToNeighbours](json)(readsFromJson)

      asObj.apply(scope, AddOperatorFactoryImpl.id) should equal(false)
      asObj.apply(scope, ValueRefFactoryImpl.id) should equal(true)
    }

    "throw RuntimeException when versioning string doesn't match what we intend to use" in {
      val versioning = s"${AddOperatorFactoryImpl.id}|${ValueRefFactoryImpl.id}"
      val versioningWithoutAddOp = s"${ValueRefFactoryImpl.id}"
      val json = JsObject(
        Seq(
          "versioning" -> JsString(versioning),
          "cache" -> JsObject(
            Seq(
              (s"Scope(0,0,0,0,0,0,0,1,0)|${AddOperatorFactoryImpl.id}", JsBoolean(false)),
              (s"Scope(0,0,0,1,0,0,0,1,0)|${ValueRefFactoryImpl.id}", JsBoolean(false)),
              (s"Scope(0,0,0,0,0,0,0,1,0)|${ValueRefFactoryImpl.id}", JsBoolean(true))
            )
          )
        )
      )

      val readsFromJson = readsMemoizeScopeToNeighbours(versioningWithoutAddOp)(factoryIdToFactoryStub)
      an[RuntimeException] should be thrownBy  Memoize2Impl.read[MemoizeScopeToNeighbours](json)(readsFromJson)
    }
  }

  private val scope = Scope(height = 1)
  private val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
  private val addOperatorFactoryImpl = injector.getInstance(classOf[AddOperatorFactoryImpl])
  private val valueRefFactoryImpl = injector.getInstance(classOf[ValueRefFactoryImpl])

  private def createSut = {
    implicit val factoryIdToFactory = factoryIdToFactoryStub
    val sut = new MemoizeScopeToNeighbours(versioning = "test")
    (sut, factoryIdToFactory)
  }

  private def factoryIdToFactoryStub = {
    val stub = mock[FactoryIdToFactory]
    when(stub.convert(AddOperatorFactoryImpl.id)).thenReturn(addOperatorFactoryImpl)
    when(stub.convert(ValueRefFactoryImpl.id)).thenReturn(valueRefFactoryImpl)
    stub
  }
}