package nodes.helpers

trait IMemoizeDi[TKey, TValue] {
  def getOrElseUpdate(key: TKey, op: TValue): TValue
  def size: Int
}
