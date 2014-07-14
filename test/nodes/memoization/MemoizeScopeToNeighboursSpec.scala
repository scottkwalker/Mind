package nodes.memoization

import nodes.ValueRefFactoryImpl
import nodes.helpers.Scope
import nodes.legalNeighbours.FactoryIdToFactory
import play.api.libs.json._
import utils.helpers.UnitSpec

class MemoizeScopeToNeighboursSpec extends UnitSpec {
  "apply" should {
    "return expected" in {
      val scope = Scope()
      val neighbours = Seq[Int](ValueRefFactoryImpl.id)
      implicit val factoryIdToFactory = mock[FactoryIdToFactory]
      val sut = new MemoizeScopeToNeighbours()

      sut.apply(key1 = scope, key2 = neighbours) should equal(Seq.empty)
    }

    "return the same result when called twice" in {
      val scope = Scope()
      val neighbours = Seq[Int](ValueRefFactoryImpl.id)
      implicit val factoryIdToFactory = mock[FactoryIdToFactory]
      val sut = new MemoizeScopeToNeighbours()

      sut.apply(key1 = scope, key2 = neighbours) should equal(Seq.empty)
      sut.apply(key1 = scope, key2 = neighbours) should equal(Seq.empty)
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
                  ("Scope(0,0,0,0,0,0,0,0,0)|List(7)",
                    JsObject(Seq(("neighbours", JsArray()))))
                )
              )
              )
          )
        )
      )
    }
  }

  "read" should {
    "convert json to usable object" in pending
  }
}
