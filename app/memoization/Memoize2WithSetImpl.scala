package memoization

import play.api.libs.json._
import serialization.JsonDeserialiser

abstract class Memoize2WithSetImpl[TKey1, TKey2](
    private val versioning: String = "unset" // For versioning purposes save something unique such as the list of all neighbour ids.
    )(implicit cacheFormat: Writes[Set[String]]) extends Memoize2WithSet[TKey1, TKey2] {

  private var cache: Set[String] = Set.empty[String]

  override def contains(implicit key1: TKey1, key2: TKey2): Boolean = {
    cache.contains(combineKeys)
  }

  override def add(implicit key1: TKey1, key2: TKey2): Unit = {
    cache += combineKeys
  }

  /**
   * Thread-safe memoization for a function.
   *
   * This works like a lazy val indexed by the input value. The memo
   * is held as part of the state of the returned function, so keeping
   * a reference to the function will keep a reference to the
   * (unbounded) memo table. The memo table will never forget a
   * result, and will retain a reference to the corresponding input
   * values as well.
   *
   * If the computation has side-effects, they will happen exactly
   * once per input, even if multiple threads attempt to memoize the
   * same input at one time, unless the computation throws an
   * exception. If an exception is thrown, then the result will not be
   * stored, and the computation will be attempted again upon the next
   * access. Only one value will be computed at a time. The overhead
   * required to ensure that the effects happen only once is paid only
   * in the case of a miss (once per input over the life of the memo
   * table). Computations for different input values will not block
   * each other.
   *
   * The combination of these factors means that this method is useful
   * for functions that will only ever be called on small numbers of
   * inputs, are expensive compared to a hash lookup and the memory
   * overhead, and will be called repeatedly.
   */
  // Combine keys into a delimited string as strings are lowest common denominator.
  private[this] def combineKeys(implicit key1: TKey1, key2: TKey2) = s"$key1|$key2"

  override def add(setOfKeys: Set[String]): Unit = {
    cache = setOfKeys
  }

  override def write: JsValue = Json.obj(
    "versioning" -> Json.toJson(versioning),
    "cache" -> Json.toJson(cache)
  )

  override def size: Int = cache.size
}

object Memoize2WithSetImpl {

  def read[T](json: JsValue)(implicit fromJson: Reads[T]): T = JsonDeserialiser.deserialize(json)
}