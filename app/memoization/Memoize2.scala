package memoization

import play.api.libs.json.JsValue
import scala.concurrent.Future

trait Memoize2[-TKey1, -TKey2, +TOutput] {

  def f(key: TKey1, key2: TKey2): Future[TOutput]

  def apply(implicit key1: TKey1, key2: TKey2): Future[TOutput]

  def write: JsValue
}