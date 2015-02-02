package memoization

import play.api.libs.json.JsValue

trait Memoize2WithSet[TKey1, TKey2] {

  def apply(implicit key1: TKey1, key2: TKey2): Boolean

  def add(implicit key1: TKey1, key2: TKey2): Unit

  def add(setOfKeys: Set[String]): Unit

  def write: JsValue

  // Take care when the TOutput is of type Future as you may not intend to block.
  def size: Int
}
