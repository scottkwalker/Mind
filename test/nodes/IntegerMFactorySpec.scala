package nodes

import nodes.helpers._
import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar
import com.google.inject.{Guice, Injector}
import ai.helpers.TestAiModule
import org.scalatest.Matchers._
import ai.IRandomNumberGenerator
import org.easymock.EasyMock._

class IntegerMFactorySpec extends WordSpec with EasyMockSugar {
  val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)
  val factory = injector.getInstance(classOf[IntegerMFactory])

  "neighbours" should {
    "have no possible children" in {
      factory.neighbours.length should equal(0)
    }
  }

  "create" should {
    "return instance of this type" in {
      // Arrange
      val s = strictMock[IScope]
      expecting {}

      whenExecuting(s) {
        // Act
        val instance = factory.create(s)

        // Assert
        instance shouldBe a[IntegerM]
      }
    }
  }

  "populateMemoizationMaps" should {
    "call memoizeCanTerminateInStepsRemaining on populateMemoizationMapsStrategy once" in {
      // Arrange
      val pmm = strictMock[IPopulateMemoizationMaps]

      class TestDevModule(pmm: IPopulateMemoizationMaps) extends DevModule(randomNumberGenerator = mock[IRandomNumberGenerator]) {
        override def bindIPopulateMemoizationMaps = bind(classOf[IPopulateMemoizationMaps]).toInstance(pmm)
      }

      val injector: Injector = Guice.createInjector(new TestDevModule(pmm), new TestAiModule)
      val sut = injector.getInstance(classOf[IntegerMFactory])

      expecting {
        pmm.memoizeCanTerminateInStepsRemaining(anyObject[IMemoizeDi[IScope, Boolean]],
          anyObject[IntegerMFactory],
          anyInt(),
          anyInt(),
          anyInt(),
          anyInt(),
          anyInt()).once()
      }

      whenExecuting(pmm) {
        // Act & Assert
        sut.populateMemoizationMaps(2, 2, 2, 2, 2)
      }
    }
  }
}