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
      val map = MemoizeDi[IScope, Boolean]
      val ccn = strictMock[ICreateChildNodes]
      val s = strictMock[IScope]

      val pmm: IPopulateMemoizationMaps = new PopulateMemoizationMaps()

      expecting {
        ccn.canTerminateInStepsRemaining(anyObject[IScope]).andReturn(true)
      }

      whenExecuting(ccn, s) {
        // Act & assert
        pmm.memoizeCanTerminateInStepsRemaining(map, ccn, s)
      }
    }

    "update map with new entry for a Scope" in {
      // Arrange
      val expected = true
      val map = MemoizeDi[IScope, Boolean]
      val ccn = strictMock[ICreateChildNodes]
      val s = strictMock[IScope]

      val pmm: IPopulateMemoizationMaps = new PopulateMemoizationMaps()

      expecting {
        ccn.canTerminateInStepsRemaining(anyObject[IScope]).andReturn(expected)
      }

      whenExecuting(ccn, s) {
        // Act
        pmm.memoizeCanTerminateInStepsRemaining(map, ccn, s)

        // Assert
        assert(map.store.size == 1)
        assert(map.store(s) == expected)
      }
    }

    "call canTerminateInStepsRemaining once for every combination" in {
      // Arrange
      val numVals = 2
      val numFuncs = 2
      val numObjects = 2
      val maxExpressionsInFunc = 2
      val maxFuncsInObject = 2
      val maxParamsInFunc = 2
      val maxDepth = 2
      val expected = (numVals + 1) * (numFuncs + 1) * (numObjects + 1) * (maxExpressionsInFunc + 1) * (maxFuncsInObject + 1) * (maxParamsInFunc + 1) * (maxDepth + 1)
      val map = MemoizeDi[IScope, Boolean]
      val ccn = strictMock[ICreateChildNodes]

      val pmm: IPopulateMemoizationMaps = new PopulateMemoizationMaps()

      expecting {
        ccn.canTerminateInStepsRemaining(anyObject[IScope]).andReturn(true).times(expected)
      }

      whenExecuting(ccn) {
        // Act
        pmm.memoizeCanTerminateInStepsRemaining(map, ccn, numVals, numFuncs, numObjects, maxExpressionsInFunc, maxFuncsInObject, maxParamsInFunc, maxDepth)

        // Assert
        assert(map.store.size == expected)
      }
    }
  }
}