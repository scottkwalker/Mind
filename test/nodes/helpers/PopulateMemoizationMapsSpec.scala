package nodes.helpers

import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar
import com.google.inject.{Guice, Injector}
import ai.helpers.TestAiModule
import org.scalatest.Matchers._


class PopulateMemoizationMapsSpec extends WordSpec with EasyMockSugar {
  "IoC" should {
    "init an instance of PopulateMemoizationMaps" in {
      // Arrange
      val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)

      // Act
      val instance = injector.getInstance(classOf[IPopulateMemoizationMaps])

      // Assert
      instance shouldBe a [PopulateMemoizationMaps]
    }
  }
}