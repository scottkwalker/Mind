package memoization

import composition.TestComposition

class GeneratorImplSpec extends TestComposition {

  "generate" must {
    "return true" in {
      whenReady(generator.generate) {
        _ must equal(true)
      }
    }
  }

  private def generator = testInjector().getInstance(classOf[Generator])
}
