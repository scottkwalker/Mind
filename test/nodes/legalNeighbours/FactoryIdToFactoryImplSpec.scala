package nodes.legalNeighbours

import utils.helpers.UnitSpec

final class FactoryIdToFactoryImplSpec extends UnitSpec {

  "convert" should {
    "throw when id is not in the list" in {
      a[RuntimeException] should be thrownBy injector.getInstance(classOf[FactoryIdToFactory]).convert(-1)
    }
  }
}
