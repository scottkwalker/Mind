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
      val scope = Scope(depth = 0, maxDepth = 1)
      val neighbours = Seq[Int](AddOperatorFactoryImpl.id, ValueRefFactoryImpl.id)
      val addOperatorFactoryImpl = injector.getInstance(classOf[AddOperatorFactoryImpl])
      val valueRefFactoryImpl = injector.getInstance(classOf[ValueRefFactoryImpl])
      implicit val factoryIdToFactory = mock[FactoryIdToFactory]
      when(factoryIdToFactory.convert(AddOperatorFactoryImpl.id)).thenReturn(addOperatorFactoryImpl)
      when(factoryIdToFactory.convert(ValueRefFactoryImpl.id)).thenReturn(valueRefFactoryImpl)
      val sut = new MemoizeScopeToNeighbours()

      sut.apply(key1 = scope, key2 = neighbours) should equal(Seq(ValueRefFactoryImpl.id))
    }

    "only runs the function once for the same input" in {
      val scope = Scope(depth = 0, maxDepth = 1)
      val neighbours = Seq[Int](AddOperatorFactoryImpl.id, ValueRefFactoryImpl.id)
      val addOperatorFactoryImpl = injector.getInstance(classOf[AddOperatorFactoryImpl])
      val valueRefFactoryImpl = injector.getInstance(classOf[ValueRefFactoryImpl])
      implicit val factoryIdToFactory = mock[FactoryIdToFactory]
      when(factoryIdToFactory.convert(AddOperatorFactoryImpl.id)).thenReturn(addOperatorFactoryImpl)
      when(factoryIdToFactory.convert(ValueRefFactoryImpl.id)).thenReturn(valueRefFactoryImpl)
      val sut = new MemoizeScopeToNeighbours()

      // TODO extract the above to a private def that returns a tuple of sut and the factoryIdToFactory,
      // so that we can verify on the mock

      sut.apply(key1 = scope, key2 = neighbours) should equal(Seq(ValueRefFactoryImpl.id))
      sut.apply(key1 = scope, key2 = neighbours) should equal(Seq(ValueRefFactoryImpl.id))

      verify(factoryIdToFactory, times(1)).convert(AddOperatorFactoryImpl.id)
      verify(factoryIdToFactory, times(1)).convert(ValueRefFactoryImpl.id)
    }
  }

  "write" should {
    "return the expected json" in {
      val scope = Scope()
      val neighbours = Seq[Int](ValueRefFactoryImpl.id)
      implicit val factoryIdToFactory = mock[FactoryIdToFactory]
      val sut = new MemoizeScopeToNeighbours()
      sut.apply(key1 = scope, key2 = neighbours) should equal(Seq.empty)

      sut.write should equal(
        JsObject(
          Seq(
            ("cache",
              JsObject(
                Seq(
                  (s"Scope(0,0,0,0,0,0,0,0,0)|List(${ValueRefFactoryImpl.id})", JsArray())
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
      val scope = Scope()
      val neighbours = Seq[Int](ValueRefFactoryImpl.id)
      val valueRefFactoryImpl = injector.getInstance(classOf[ValueRefFactoryImpl])
      implicit val factoryIdToFactory = mock[FactoryIdToFactory]
      when(factoryIdToFactory.convert(ValueRefFactoryImpl.id)).thenReturn(valueRefFactoryImpl)

      implicit val mapOfNeighboursFromJson: Reads[MemoizeScopeToNeighbours] =
        (__ \ "cache").read[Map[String, Seq[Int]]].map {
          keyValueMap =>
            val cache = keyValueMap.map {
              case (k, v) => k -> Right[CountDownLatch, Seq[Int]](v) // TODO convert key to Scope | List
            }
            new MemoizeScopeToNeighbours(cache)
        }

      val json = JsObject(
        Seq(
          ("cache",
            JsObject(
              Seq(
                (s"Scope(0,0,0,0,0,0,0,0,0)|List(${ValueRefFactoryImpl.id})", JsArray())
              )
            )
          )
        )
      )

      val asObj: MemoizeScopeToNeighbours = Memoize2Impl.read[MemoizeScopeToNeighbours](json)

      asObj(scope, neighbours) should equal(Seq.empty)
    }
  }

  private val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
}
