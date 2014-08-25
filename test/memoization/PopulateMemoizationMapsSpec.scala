package memoization

import composition.TestComposition

final class PopulateMemoizationMapsSpec extends TestComposition {

  /*
    private val addOperatorFactory = injector.getInstance(classOf[AddOperatorFactoryImpl])
    private val functionMFactory = injector.getInstance(classOf[FunctionMFactoryImpl])
    private val integerMFactory = injector.getInstance(classOf[IntegerMFactoryImpl])
    private val nodeTreeFactory = injector.getInstance(classOf[NodeTreeFactoryImpl])
    private val objectDefFactory = injector.getInstance(classOf[ObjectDefFactoryImpl])
    private val valDclInFunctionParamFactory = injector.getInstance(classOf[ValDclInFunctionParamFactoryImpl])
    private val valueRefFactory = injector.getInstance(classOf[ValueRefFactoryImpl])
  */
  "IoC" must {
    "init an instance of PopulateMemoizationMaps" in {
      // Act
      val instance = injector.getInstance(classOf[IPopulateMemoizationMaps])

      // Assert
      instance mustBe a[PopulateMemoizationMaps]
    }
  }

  "memoizeCanTerminateInStepsRemaining" must {
    "uncomment the below" in pending
  }

  /*
      "memoizeCanTerminateInStepsRemaining" must {

        "call canTerminateInStepsRemaining in factory" in {
          // Arrange
          val ccn = strictMock[ICreateChildNodes]
          val s = strictMock[IScope]
          val map: IMemoizeDi[IScope, Boolean] = strictMock[IMemoizeDi[IScope, Boolean]]

          val store: mutable.Map[IScope, Boolean] = mutable.Map.empty
          val pmm: IPopulateMemoizationMaps = new PopulateMemoizationMaps(addOperatorFactory,
            functionMFactory,
            integerMFactory,
            nodeTreeFactory,
            objectDefFactory,
            valDclInFunctionParamFactory,
            valueRefFactory)

          expecting {
            ccn.mapOfCanTerminateInStepsRemaining andReturn map
            ccn.canTerminateInStepsRemaining(anyObject[IScope]).andReturn(true)
          }

          whenExecuting(ccn, s, map) {
            // Act & assert
            pmm.memoizeCanTerminateInStepsRemaining(ccn, s)
          }
        }

        "update map with new entry for a Scope" in {
          // Arrange
          val expected = true
          val ccn = strictMock[ICreateChildNodes]
          val s = strictMock[IScope]
          val map: IMemoizeDi[IScope, Boolean] = strictMock[IMemoizeDi[IScope, Boolean]]

          val store: mutable.Map[IScope, Boolean] = mutable.Map.empty
          val pmm: IPopulateMemoizationMaps = new PopulateMemoizationMaps(addOperatorFactory,
            functionMFactory,
            integerMFactory,
            nodeTreeFactory,
            objectDefFactory,
            valDclInFunctionParamFactory,
            valueRefFactory)

          expecting {
            ccn.mapOfCanTerminateInStepsRemaining andReturn map
            ccn.canTerminateInStepsRemaining(anyObject[IScope]).andReturn(expected)
          }

          whenExecuting(ccn, s, map) {
            // Act
            pmm.memoizeCanTerminateInStepsRemaining(ccn, s)
          }

          // Assert
          assert(store.size == 1)
          assert(store(s) == expected)
        }

        "call canTerminateInStepsRemaining once for every combination" in {
          // Arrange
          val numVals = 0
          val numFuncs = 0
          val numObjects = 0
          val maxExpressionsInFunc = 0
          val maxFuncsInObject = 0
          val maxParamsInFunc = 0
          val depth = 1
          val maxObjectsInTree = 0
          val expected = (numVals + 1) * (numFuncs + 1) * (numObjects + 1) * (maxExpressionsInFunc + 1) * (maxFuncsInObject + 1) * (maxParamsInFunc + 1) * (depth + 1) * (maxObjectsInTree + 1)

          val ccn = strictMock[ICreateChildNodes]

          val memoizeDi = MemoizeDi[IScope, Boolean]
          val pmm: IPopulateMemoizationMaps = new PopulateMemoizationMaps(addOperatorFactory, integerMFactory)

          expecting {
            ccn.mapOfCanTerminateInStepsRemaining.andReturn(memoizeDi)//.anyTimes()//.times(expected)
            ccn.canTerminateInStepsRemaining(anyObject[IScope]).andReturn(true).times(1)//.anyTimes()//.times(expected)
          }

          whenExecuting(ccn) {
            // Act
            pmm.memoizeCanTerminateInStepsRemaining(ccn, numVals, numFuncs, numObjects, maxExpressionsInFunc, maxFuncsInObject, maxParamsInFunc, depth, maxObjectsInTree)

            // Assert
            assert(memoizeDi.store.size == expected)
          }
        }
      }

      "run" must {
        "populate map for each factory" in {
          val maxExpressionsInFunc = 1
          val maxFuncsInObject = 1
          val maxParamsInFunc = 1
          val depth = 1
          val maxObjectsInTree = 1

          val pmm: IPopulateMemoizationMaps = new PopulateMemoizationMaps(addOperatorFactory,
            functionMFactory,
            integerMFactory,
            nodeTreeFactory,
            objectDefFactory,
            valDclInFunctionParamFactory,
            valueRefFactory)

          // Act
          pmm.run(maxExpressionsInFunc, maxFuncsInObject, maxParamsInFunc, depth, maxObjectsInTree)

          // Assert
          assert(addOperatorFactory.mapOfCanTerminateInStepsRemaining.size > 0)
          assert(functionMFactory.mapOfCanTerminateInStepsRemaining.size > 0)
          assert(integerMFactory.mapOfCanTerminateInStepsRemaining.size > 0)
          assert(nodeTreeFactory.mapOfCanTerminateInStepsRemaining.size > 0)
          assert(objectDefFactory.mapOfCanTerminateInStepsRemaining.size > 0)
          assert(valDclInFunctionParamFactory.mapOfCanTerminateInStepsRemaining.size > 0)
          assert(valueRefFactory.mapOfCanTerminateInStepsRemaining.size > 0)
        }
      }*/
}