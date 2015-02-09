package controllers

import composition.StubGeneratorBinding
import composition.TestComposition
import models.common.IScope
import models.common.PopulateRequest
import models.common.Scope
import org.mockito.Matchers.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import play.api.libs.json.Json
import play.api.test.Helpers.BAD_REQUEST
import play.api.test.Helpers.OK
import play.api.test.Helpers.contentAsString
import play.api.test.FakeRequest
import play.api.test.WithApplication

final class PopulateUnitSpec extends TestComposition {

  "present" must {
    "return 200" in new WithApplication {
      whenReady(present) { r =>
        r.header.status must equal(OK)
      }(config = patienceConfig)
    }

    "contain a form that POSTs to the expected action" in new WithApplication {
      contentAsString(present)(timeout) must include( """form action="/mind/populate" method="POST"""")
    }
  }

  "calculate" must {
    "return bad request when submission is empty" in new WithApplication {
      val emptyRequest = FakeRequest().withFormUrlEncodedBody()
      val result = populate.calculate(emptyRequest)
      whenReady(result) { r =>
        r.header.status must equal(BAD_REQUEST)
      }(config = patienceConfig)
    }

    "return ok when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults()
      val result = populate.calculate(validRequest)
      whenReady(result) { r =>
        r.header.status must equal(OK)
      }(config = patienceConfig)
    }

    "call lookupChildren.fetch when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val generator = new StubGeneratorBinding
      val injector = testInjector(
        generator
      )
      val sut = injector.getInstance(classOf[Populate])

      val result = sut.calculate(validRequest)
      whenReady(result) { r =>
        verify(generator.stub, times(1)).calculateAndUpdate(any[IScope])
      }(config = patienceConfig)
    }
  }

  private def populate = testInjector().getInstance(classOf[Populate])

  private def present = {
    val emptyRequest = FakeRequest()
    populate.present(emptyRequest)
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
