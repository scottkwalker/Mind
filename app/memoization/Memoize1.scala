package memoization

import play.api.libs.json.JsValue

trait Memoize1[-TKey, +TOutput] {

  def f(key: TKey): TOutput

  def apply(key: TKey): TOutput

  def write: JsValue

  def size: Int
}
