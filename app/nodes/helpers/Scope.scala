package nodes.helpers

import com.google.inject.name.Named
import com.google.inject.Inject

case class Scope (val numVals: Integer = 0,
  val numFuncs: Integer = 0,
  val numObjects: Integer = 0,
  val stepsRemaining: Integer = 0,
  val maxObjects: Integer = 0) {
  def incrementVals: Scope = Scope(numVals = numVals + 1, numFuncs, numObjects, stepsRemaining, maxObjects)
  def incrementFuncs: Scope = Scope(numVals, numFuncs = numFuncs + 1, numObjects, stepsRemaining, maxObjects)
  def incrementObjects: Scope = Scope(numVals, numFuncs, numObjects = numObjects + 1, stepsRemaining, maxObjects)
  def decrementStepsRemaining: Scope = Scope(numVals, numFuncs, numObjects, stepsRemaining = stepsRemaining - 1, maxObjects)
  val noStepsRemaining = stepsRemaining == 0
}