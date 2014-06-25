package nodes.memoization

import play.api.libs.json.JsValue

trait Memoize2[-T1, -T2, +T3] {
  def f(key: T1, t2: T2): T3

  def apply(key: T1, t2: T2): T3

  def write: JsValue
}
