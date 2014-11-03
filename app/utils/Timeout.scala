package utils

import scala.concurrent.duration.{FiniteDuration, SECONDS}

object Timeout {

  val finiteTimeout = FiniteDuration(30, SECONDS)
}
