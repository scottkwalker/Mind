package nodes.helpers

import com.google.inject.name.Named
import com.google.inject.Inject

case class Scope(val numVals: Integer = 0,
                 val accumulatorLength: Integer = 0,
                 val numFuncs: Integer = 0,
                 val numObjects: Integer = 0,
                 val stepsRemaining: Integer = 0,
                 val maxExpressionsInFunc: Integer = 0,
                 val maxFuncsInObject: Integer = 0) {
  def incrementVals: Scope = Scope(numVals = numVals + 1, accumulatorLength, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncsInObject)
  def incrementAccumulatorLength: Scope = Scope(numVals, accumulatorLength = accumulatorLength + 1, numFuncs, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncsInObject)
  def incrementFuncs: Scope = Scope(numVals, accumulatorLength, numFuncs = numFuncs + 1, numObjects, stepsRemaining, maxExpressionsInFunc, maxFuncsInObject)
  def incrementObjects: Scope = Scope(numVals, accumulatorLength, numFuncs, numObjects = numObjects + 1, stepsRemaining, maxExpressionsInFunc, maxFuncsInObject)
  def decrementStepsRemaining: Scope = Scope(numVals, accumulatorLength, numFuncs, numObjects, stepsRemaining = stepsRemaining - 1, maxExpressionsInFunc, maxFuncsInObject)
  val noStepsRemaining = stepsRemaining == 0
  def funcHasSpaceForChildren = accumulatorLength < maxExpressionsInFunc
  def objHasSpaceForChildren = accumulatorLength < maxFuncsInObject
}