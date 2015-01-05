package controllers

import composition.{StubGenerator, StubLookupChildren, TestComposition}
import memoization.{Generator, LookupChildren}
import models.common.{IScope, PopulateRequest, Scope}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify}
import play.api.libs.json.Json
import play.api.test.Helpers.{BAD_REQUEST, OK, contentAsString}
import play.api.test.{FakeRequest, WithApplication}
import replaceEmpty.TypeTreeFactoryImpl
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global

final class PopulateUnitSpec extends TestComposition {

  "present" must {
    "return 200" in new WithApplication {
      whenReady(present, browserTimeout) { r =>
        r.header.status must equal(OK)
      }
    }

    "contain a form that POSTs to the expected action" in new WithApplication {
      contentAsString(present)(timeout) must include( """form action="/mind/populate" method="POST"""")
    }
  }

  "calculate" must {
    "return bad request when submission is empty" in new WithApplication {
      val emptyRequest = FakeRequest().withFormUrlEncodedBody()
      val result = populate.calculate(emptyRequest)
      whenReady(result, browserTimeout) { r =>
        r.header.status must equal(BAD_REQUEST)
      }
    }

    "return ok when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults()
      val result = populate.calculate(validRequest)
      whenReady(result, browserTimeout) { r =>
        r.header.status must equal(OK)
      }
    }

    "call lookupChildren.fetch when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val generator = mock[Generator]
      val injector = testInjector(new StubGenerator(generator))
      val sut = injector.getInstance(classOf[Populate])

      val result = sut.calculate(validRequest)
      whenReady(result, browserTimeout) { r =>
        verify(generator, times(1)).generate
      }
    }
  }

  private def populate = testInjector().getInstance(classOf[Populate])
  private def present = {
    val emptyRequest = FakeRequest()
    populate.present(emptyRequest)
  }
  private val scopeDefault = Scope(
    numVals = 1,
    numFuncs = 2,
    numObjects = 3,
    height = 4,
    maxExpressionsInFunc = 5,
    maxFuncsInObject = 6,
    maxParamsInFunc = 7,
    maxObjectsInTree = 8
  )

  private def requestWithDefaults(scope: Scope = scopeDefault, currentNode: Int = 1) = {
    val request = PopulateRequest(scope, currentNode)
    FakeRequest().withJsonBody(Json.toJson(request))
  }
}
