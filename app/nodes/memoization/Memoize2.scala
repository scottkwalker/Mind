package nodes.memoization

import play.api.libs.json.JsValue

trait Memoize2[-TKey1, -TKey2, +TOutput] {

  def f(key: TKey1, key2: TKey2): TOutput

  def apply(implicit key1: TKey1, key2: TKey2): TOutput

  def write: JsValue
}