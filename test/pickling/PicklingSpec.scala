package pickling

import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mock.EasyMockSugar
import nodes.helpers.{IScope, MemoizeDi, Scope}
import scala.collection.mutable


class PicklingSpec extends WordSpec with EasyMockSugar with Matchers {
  "list of primitives" should {
    import scala.pickling._
    val input = List(1, 2, 3, 4)

    "pickle into a json type" in {
      import scala.pickling.json._
      val pickled = input.pickle

      pickled shouldBe a[JSONPickle]
    }

    "unpickle json into the original input" in {
      import scala.pickling.json._
      val pickled = input.pickle

      val unpickled = pickled.unpickle[List[Int]]

      assert(unpickled == input)
    }

    "pickle into a binary type" in {
      import scala.pickling.binary._
      val pickled = input.pickle

      pickled shouldBe a[BinaryPickle]
    }

    "unpickle binary into the original input" in {
      import scala.pickling.binary._
      val pickled = input.pickle

      val unpickled = pickled.unpickle[List[Int]]

      assert(unpickled == input)
    }
  }

  "Scope" should {
    import scala.pickling._

    val input = Scope(numVals = 1,
      numFuncs = 2,
      numObjects = 3,
      depth = 4,
      maxExpressionsInFunc = 5,
      maxFuncsInObject = 6,
      maxParamsInFunc = 7,
      maxDepth = 8,
      maxObjectsInTree = 9)


    "pickle into a json type" in {
      import scala.pickling.json._
      val pickled = input.pickle

      pickled shouldBe a[JSONPickle]
    }

    "unpickle into the original input" in {
      import scala.pickling.json._
      val pickled = input.pickle

      val unpickled = pickled.unpickle[Scope]

      assert(unpickled == input)
    }

    "pickle into a binary type" in {
      import scala.pickling.binary._
      val pickled = input.pickle

      pickled shouldBe a[BinaryPickle]
    }

    "unpickle binary into the original input" in {
      import scala.pickling.binary._
      val pickled = input.pickle

      val unpickled = pickled.unpickle[Scope]

      assert(unpickled == input)
    }
  }

  "Map of Scope" should {
    import scala.pickling._

    val input0 = Scope(numVals = 1,
      numFuncs = 2,
      numObjects = 3,
      depth = 4,
      maxExpressionsInFunc = 5,
      maxFuncsInObject = 6,
      maxParamsInFunc = 7,
      maxDepth = 8,
      maxObjectsInTree = 9)
    val input1 = Scope(numVals = 11,
      numFuncs = 12,
      numObjects = 13,
      depth = 14,
      maxExpressionsInFunc = 15,
      maxFuncsInObject = 16,
      maxParamsInFunc = 17,
      maxDepth = 18,
      maxObjectsInTree = 19)
    val store: mutable.Map[IScope, Boolean] = mutable.Map(input0 -> true, input1 -> false)


    "pickle into a json type" in {
      import scala.pickling.json._
      val pickled = store.pickle

      pickled shouldBe a[JSONPickle]
    }

    "unpickle into the original input" in {
      import scala.pickling.json._
      val pickled = store.pickle

      val unpickled = pickled.unpickle[mutable.Map[IScope, Boolean]]

      assert(unpickled == store)
    }

    "pickle into a binary type" in {
      import scala.pickling.binary._
      val pickled = store.pickle

      pickled shouldBe a[BinaryPickle]
    }

    "unpickle binary into the original input" in {
      import scala.pickling.binary._
      val pickled = store.pickle

      val unpickled = pickled.unpickle[mutable.Map[IScope, Boolean]]

      assert(unpickled == store)
    }
  }
}
