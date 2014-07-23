package utils.helpers

import org.scalatest.{ParallelTestExecution, Matchers, WordSpec}
import org.scalatest.mock.MockitoSugar
import org.scalatest.concurrent.ScalaFutures

abstract class UnitSpec extends WordSpec with Matchers with MockitoSugar with ScalaFutures //with ParallelTestExecution