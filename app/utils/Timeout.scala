package utils

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.SECONDS

object Timeout {

  val finiteTimeout = FiniteDuration(30, SECONDS)
}
