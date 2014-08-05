package utils.helpers

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}

abstract class UnitSpec extends WordSpec with Matchers with MockitoSugar with ScalaFutures