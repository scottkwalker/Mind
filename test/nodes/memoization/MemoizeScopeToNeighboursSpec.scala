package nodes.memoization

import java.util.concurrent.CountDownLatch

import com.google.inject.{Guice, Injector}
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import nodes.{AddOperatorFactoryImpl, ValueRefFactoryImpl}
import nodes.helpers.Scope
import nodes.legalNeighbours.FactoryIdToFactory
import play.api.libs.json._
import utils.helpers.UnitSpec
import org.mockito.Mockito._

class MemoizeScopeToNeighboursSpec extends UnitSpec {
  "apply" should {
    "return only values that are valid for this scope" in {
      val (sut, _) = createSut

      sut.apply(key1 = scope, key2 = neighbours) should equal(Seq(ValueRefFactoryImpl.id))
    }

    "only runs the function once for the same input" in {
      val (sut, factoryIdToFactory) = createSut
      sut.apply(key1 = scope, key2 = neighbours) should equal(Seq(ValueRefFactoryImpl.id))
      sut.apply(key1 = scope, key2 = neighbours) should equal(Seq(ValueRefFactoryImpl.id))

      verify(factoryIdToFactory, times(1)).convert(AddOperatorFactoryImpl.id)
      verify(factoryIdToFactory, times(1)).convert(ValueRefFactoryImpl.id)
    }
  }

  "write" should {
    "return the expected json" in {
      val (sut, _) = createSut
      sut.apply(key1 = scope, key2 = neighbours) should equal(Seq(ValueRefFactoryImpl.id))

      sut.write should equal(
        JsObject(
          Seq(
            ("cache",
              JsObject(
                Seq(
                  (s"Scope(0,0,0,0,0,0,0,1,0)|List(${AddOperatorFactoryImpl.id}, ${ValueRefFactoryImpl.id})", JsArray(Seq(ValueRefFactoryImpl.id).map(n => JsNumber(n)))),
                  (s"Scope(0,0,0,1,0,0,0,1,0)|List(${ValueRefFactoryImpl.id})", JsArray())
                )
              )
            )
          )
        )
      )
    }
  }

  "read" should {
    "convert from json to usable object" in {
      import nodes.memoization.MemoizeScopeToNeighbours.mapOfNeighboursFromJson

      val json = JsObject(
        Seq(
          ("cache",
            JsObject(
              Seq(
                (s"Scope(0,0,0,0,0,0,0,1,0)|List(${AddOperatorFactoryImpl.id}, ${ValueRefFactoryImpl.id})", JsArray(Seq(ValueRefFactoryImpl.id).map(n => JsNumber(n)))),
                (s"Scope(0,0,0,1,0,0,0,1,0)|List(${ValueRefFactoryImpl.id})", JsArray())
              )
            )
          )
        )
      )

      val asObj: MemoizeScopeToNeighbours = Memoize2Impl.read[MemoizeScopeToNeighbours](json)(mapOfNeighboursFromJson(factoryIdToFactoryStub))

      asObj(scope, neighbours) should equal(Seq(ValueRefFactoryImpl.id))
    }
  }

  private val scope = Scope(depth = 0, maxDepth = 1)
  private val neighbours = Seq[Int](AddOperatorFactoryImpl.id, ValueRefFactoryImpl.id)
  private val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
  private val addOperatorFactoryImpl = injector.getInstance(classOf[AddOperatorFactoryImpl])
  private val valueRefFactoryImpl = injector.getInstance(classOf[ValueRefFactoryImpl])
  private def factoryIdToFactoryStub = {
    val stub = mock[FactoryIdToFactory]
    when(stub.convert(AddOperatorFactoryImpl.id)).thenReturn(addOperatorFactoryImpl)
    when(stub.convert(ValueRefFactoryImpl.id)).thenReturn(valueRefFactoryImpl)
    stub
  }
  private def createSut = {
    implicit val factoryIdToFactory = factoryIdToFactoryStub
    val sut = new MemoizeScopeToNeighbours()
    (sut, factoryIdToFactory)
  }
}
