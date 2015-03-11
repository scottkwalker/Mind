package controllers

import composition.StubGeneratorBinding
import composition.TestComposition
import composition.UnitTestHelpers
import models.common.IScope
import models.common.PopulateRequest
import models.common.Scope
import org.mockito.Matchers.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.BAD_REQUEST
import play.api.test.Helpers.OK
import play.api.test.Helpers.contentAsString

final class PopulateUnitSpec extends UnitTestHelpers with TestComposition {

  "present" must {
    "return 200" in {
      whenReady(present) { r =>
        r.header.status must equal(OK)
      }(config = patienceConfig)
    }

    "contain a form that POSTs to the expected action" in {
      contentAsString(present)(timeout) must include( """form action="/mind/populate" method="POST"""")
    }
  }

  "calculate" must {
    "return bad request if given an empty submission" in {
      val emptyRequest = FakeRequest().withFormUrlEncodedBody()
      val (populate, _) = build

      val calculated = populate.calculate(emptyRequest)

      whenReady(calculated) {
        _.header.status must equal(BAD_REQUEST)
      }(config = patienceConfig)
    }

    "return ok if given a valid submission" in {
      val validRequest = requestWithDefaults()
      val (populate, _) = build

      val calculated = populate.calculate(validRequest)

      whenReady(calculated) {
        _.header.status must equal(OK)
      }(config = patienceConfig)
    }

    "call lookupChildren.fetch once if given a valid submission" in {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val (populate, generator) = build

      val calculated = populate.calculate(validRequest)
      whenReady(calculated) { r =>
        verify(generator, times(1)).calculateAndUpdate(any[IScope])
        verifyNoMoreInteractions(generator)
      }(config = patienceConfig)
    }
  }

  private def present = {
    val emptyRequest = FakeRequest()
    val (populate, _) = build
    populate.present(emptyRequest)
  }

  private def build = {
    val generator = new StubGeneratorBinding
    val injector = testInjector(generator)
    (injector.getInstance(classOf[Populate]), generator.stub)
  }

  private def scopeDefault = Scope(
    numVals = 1,
    numFuncs = 2,
    numObjects = 3,
    height = 4,
    maxExpressionsInFunc = 5,
    maxFuncsInObject = 6,
    maxParamsInFunc = 7,
    maxObjectsInTree = 8,
    maxHeight = 10
  )

  private def requestWithDefaults(scope: Scope = scopeDefault) = {
    val request = PopulateRequest(scope)
    FakeRequest().withJsonBody(Json.toJson(request))
  }
}
