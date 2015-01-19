package memoization

import play.api.libs.json.JsValue
import scala.concurrent.Future

trait Memoize2[-TKey1, -TKey2, +TOutput] {

  def funcCalculate(key: TKey1, key2: TKey2): TOutput

  def apply(implicit key1: TKey1, key2: TKey2): TOutput

  def write: JsValue

  def size: Int // Take care when the TOutput is of type Future as you may not intend to block.
}