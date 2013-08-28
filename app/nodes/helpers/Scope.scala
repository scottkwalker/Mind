package nodes.helpers

import com.google.inject.name.Named
import com.google.inject.Inject

case class Scope (val numVals: Integer = 0,
  val numFuncs: Integer = 0,
  val numObjects: Integer = 0,
  val stepsRemaining: Integer = 0,
  val maxFuncs: Integer = 0) {
  def incrementVals: Scope = Scope(numVals = numVals + 1, numFuncs, numObjects, stepsRemaining, maxFuncs)
  def incrementFuncs: Scope = Scope(numVals, numFuncs = numFuncs + 1, numObjects, stepsRemaining, maxFuncs)
  def incrementObjects: Scope = Scope(numVals, numFuncs, numObjects = numObjects + 1, stepsRemaining, maxFuncs)
  def decrementStepsRemaining: Scope = Scope(numVals, numFuncs, numObjects, stepsRemaining = stepsRemaining - 1, maxFuncs)
  val noStepsRemaining = stepsRemaining == 0
  val objHasSpaceForChildren = numFuncs < maxFuncs
}