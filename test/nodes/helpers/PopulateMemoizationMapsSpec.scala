package nodes.helpers

import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar
import com.google.inject.{Guice, Injector}
import ai.helpers.TestAiModule
import org.scalatest.Matchers._
import org.easymock.EasyMock._


class PopulateMemoizationMapsSpec extends WordSpec with EasyMockSugar {
  "IoC" should {
    "init an instance of PopulateMemoizationMaps" in {
      // Arrange
      val injector: Injector = Guice.createInjector(new DevModule, new TestAiModule)

      // Act
      val instance = injector.getInstance(classOf[IPopulateMemoizationMaps])

      // Assert
      instance shouldBe a[PopulateMemoizationMaps]
    }
  }

  "memoizeCanTerminateInStepsRemaining" should {
    "calls canTerminateInStepsRemaining in factory" in {
      // Arrange
      val map = strictMock[IMemoizeDi[IScope, Boolean]]
      val ccn = strictMock[ICreateChildNodes]
      val s = strictMock[IScope]

      val pmm = new PopulateMemoizationMaps()

      expecting {
        ccn.canTerminateInStepsRemaining(anyObject[IScope]).andReturn(true)
      }

      whenExecuting(map, ccn, s) {
        // Act & assert
        pmm.memoizeCanTerminateInStepsRemaining(map, ccn, s)
      }
    }
    /*
        "update map with new entries for a Scope" in {
          // Arrange
          val map: IMemoizeDi[IScope, Boolean] = MemoizeDi[IScope, Boolean]
          val pmm = new PopulateMemoizationMaps()
          val ccn: ICreateChildNodes = strictMock[ICreateChildNodes]
          val scope: IScope = strictMock[IScope]

          expecting{
            ccn.canTerminateInStepsRemaining(scope)
          }

          whenExecuting(pmm, ccn, scope){
            // Act & assert
            pmm.memoizeCanTerminateInStepsRemaining(map, ccn)
          }
        }*/
  }
}