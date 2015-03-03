package utils

import models.common.IScope
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar

object ScopeHelper extends MockitoSugar {

  def scopeWithHeightRemaining = scope(hasHeightRemaining = true)

  def scopeWithoutHeightRemaining = scope(hasHeightRemaining = false)

  def scopeWithNumVals(numVals: Int) = scope(numVals = numVals)

  def scope(hasHeightRemaining: Boolean = true, numVals: Int = 0) = {
    val scope = mock[IScope]
    when(scope.hasHeightRemaining).thenReturn(hasHeightRemaining)
    when(scope.numVals).thenReturn(numVals)
    when(scope.incrementVals).thenReturn(scope)
    when(scope.decrementHeight).thenReturn(scope)
    scope
  }
}
