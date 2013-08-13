package nodes.helpers

case class Scope(val numVals: Integer = 0, val numFuncs: Integer = 0, val numObjects: Integer = 0){
  def incrementVals: Scope = Scope(numVals = numVals + 1)
  def incrementFuncs: Scope = Scope(numFuncs = numFuncs + 1)
  def incrementObjects: Scope = Scope(numObjects = numObjects + 1)
}