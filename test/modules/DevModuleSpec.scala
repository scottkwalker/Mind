package modules

import utils.helpers.UnitSpec2

final class DevModuleSpec extends UnitSpec2 {

  "configure" must {
    "create an empty memoization map when saved file does not exist" in pending

    "create an empty memoization map when saved file exist but versioning does not match" in pending

    "populate the memoization map when a saved file exists with matching versioning" in pending
  }
}
