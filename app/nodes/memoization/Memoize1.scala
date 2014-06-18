package nodes.memoization

import play.api.libs.json.JsValue

trait Memoize1[-TInput, +TOutput] {
  def apply(key: TInput): TOutput
  def write: JsValue
}

trait Memoize2[-T1, -T2, +T3] {
  def apply(key: T1, neighbours: T2): T3
  def write: JsValue
}
