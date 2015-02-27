package utils

import composition.UnitTestHelpers

final class PozIntSpec extends UnitTestHelpers {

  "contructor" must {
    "throw when value is less than 0" in {
      val value = -1
      a[RuntimeException] must be thrownBy PozInt(value)
    }

    "contain the expected value when the constructor arguments are valid for the lowest boundary" in {
      val value = 0
      val pozInt = PozInt(value)
      pozInt.value must equal(value)
    }

    "contain the expected value when the constructor arguments are valid for the highest boundary" in {
      val value = Int.MaxValue
      val pozInt = PozInt(value)
      pozInt.value must equal(value)
    }
  }
}
