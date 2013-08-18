package nodes

import com.google.inject.Inject

case class Empty @Inject() () extends Node {
  def toRawScala: String = throw new scala.RuntimeException
  def validate(stepsRemaining: Integer): Boolean = false
}