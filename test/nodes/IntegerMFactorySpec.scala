package nodes

import nodes.helpers._
import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar
import com.google.inject.{Guice, Injector}
import ai.helpers.TestAiModule
import org.scalatest.Matchers._
import ai.{IAi}
import org.easymock.EasyMock._

class IntegerMFactorySpec extends WordSpec with EasyMockSugar {
  val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)
  val factory = injector.getInstance(classOf[IntegerMFactory])

  "neighbours" should {
    "have no possible children" in {
      factory.neighbours.length should equal (0)
    }
  }

  "create" should {
    "return instance of this type" in {
      // Arrange
      val s = strictMock[IScope]
      expecting{}

      whenExecuting(s){
        // Act
        val instance = factory.create(s)

        // Assert
        instance shouldBe a [IntegerM]
      }
    }
  }

  "populateMemoizationMaps" should {
    "call memoizeCanTerminateInStepsRemaining on populateMemoizationMapsStrategy once" in {
      // Arrange
      val csn = strictMock[ICreateSeqNodes]
      val ai = strictMock[IAi]
      val pmm = strictMock[IPopulateMemoizationMaps]
      val ctisr = strictMock[IMemoizeDi[IScope, Boolean]]
      val ln = strictMock[IMemoizeDi[IScope, Seq[ICreateChildNodes]]]

      val sut = IntegerMFactory(csn, ai, pmm, ctisr, ln)

      expecting {
        pmm.memoizeCanTerminateInStepsRemaining(anyObject[IMemoizeDi[IScope, Boolean]]).once()
      }

      whenExecuting(csn, ai, pmm, ctisr, ln) {
        // Act & Assert
        sut.populateMemoizationMaps()
      }
    }
  }


}