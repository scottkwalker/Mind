package nodes.memoization

import play.api.libs.json.JsValue

trait Memoize1[-TInput, +TOutput] {
  def apply(key: TInput): TOutput
  def write: JsValue
}
