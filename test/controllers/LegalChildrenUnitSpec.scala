package controllers

import composition.{StubLookupChildren, TestComposition}
import memoization.LookupChildren
import models.common.{IScope, LookupChildrenRequest, Scope}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify}
import play.api.libs.json.Json
import play.api.test.Helpers.{BAD_REQUEST, OK, contentAsString}
import play.api.test.{FakeRequest, WithApplication}
import replaceEmpty.TypeTreeFactoryImpl
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global

final class LegalChildrenUnitSpec extends TestComposition {

  "present" must {
    "return 200" in new WithApplication {
      whenReady(present, browserTimeout) { r =>
        r.header.status must equal(OK)
      }
    }

    "contain a form that POSTs to the expected action" in new WithApplication {
      contentAsString(present)(timeout) must include( """form action="/mind/legal-children" method="POST"""")
    }
  }

  "calculate" must {
    "return bad request when submission is empty" in new WithApplication {
      val emptyRequest = FakeRequest().withFormUrlEncodedBody()
      val result = legalChildren.calculate(emptyRequest)
      whenReady(result, browserTimeout) { r =>
        r.header.status must equal(BAD_REQUEST)
      }
    }

    "return ok when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults()
      val result = legalChildren.calculate(validRequest)
      whenReady(result, browserTimeout) { r =>
        r.header.status must equal(OK)
      }
    }

    "return seq of ids when submission is valid and legal moves are found" in new WithApplication {
      val validRequest = requestWithDefaults()
      val result = legalChildren.calculate(validRequest)
      whenReady(result, browserTimeout) { r =>
        r.body.map { b =>
          Json.parse(b) must equal(Seq(TypeTreeFactoryImpl.id))
        }
      }
    }

    "return empty seq when submission is valid but no matches are in scope" in new WithApplication {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val result = legalChildren.calculate(validRequest)
      whenReady(result, browserTimeout) { r =>
        r.body.map { b =>
          Json.parse(b) must equal(Seq.empty)
        }
      }
    }

    "call lookupChildren.fetch when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val lookupChildren = mock[LookupChildren]
      val injector = testInjector(new StubLookupChildren(lookupChildren))
      val sut = injector.getInstance(classOf[LegalChildren])

      val result = sut.calculate(validRequest)
      whenReady(result, browserTimeout) { r =>
        verify(lookupChildren, times(1)).fetch(any[IScope], any[PozInt])
      }
    }
  }

  private def legalChildren =
    testInjector(new StubLookupChildren()).
      getInstance(classOf[LegalChildren])

  private def present = {
    val emptyRequest = FakeRequest()
    legalChildren.present(emptyRequest)
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

  private def requestWithDefaults(scope: Scope = scopeDefault, currentNode: Int = 1) = {
    val request = LookupChildrenRequest(scope, currentNode)
    FakeRequest().withJsonBody(Json.toJson(request))
  }
}
