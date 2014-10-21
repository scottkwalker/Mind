package controllers

import composition.{StubLookupNeighbours, TestComposition}
import replaceEmpty.NodeTreeFactoryImpl
import memoization.LookupNeighbours
import models.common.{IScope, LegalNeighboursRequest, Scope}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify}
import play.api.libs.json.Json
import play.api.test.Helpers.{BAD_REQUEST, OK, contentAsString}
import play.api.test.{FakeRequest, WithApplication}
import scala.concurrent.ExecutionContext.Implicits.global

final class LegalNeighboursUnitSpec extends TestComposition {

  "present" must {
    "return 200" in new WithApplication {
      whenReady(present) { r =>
        r.header.status must equal(OK)
      }
    }

    "contain a form that POSTs to the expected action" in new WithApplication {
      contentAsString(present)(timeout) must include( """form action="/mind/legal-neighbours" method="POST"""")
    }
  }

  "calculate" must {
    "return bad request when submission is empty" in new WithApplication {
      val emptyRequest = FakeRequest().withFormUrlEncodedBody()
      val result = legalNeighbours.calculate(emptyRequest)
      whenReady(result) { r =>
        r.header.status must equal(BAD_REQUEST)
      }
    }

    "return ok when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults()
      val result = legalNeighbours.calculate(validRequest)
      whenReady(result) { r =>
        r.header.status must equal(OK)
      }
    }

    "return seq of ids when submission is valid and legal moves are found" in new WithApplication {
      val validRequest = requestWithDefaults()
      val result = legalNeighbours.calculate(validRequest)
      whenReady(result) { r =>
        r.body.map { b =>
          Json.parse(b) must equal(Seq(NodeTreeFactoryImpl.id))
        }
      }
    }

    "return empty seq when submission is valid but no matches are in scope" in new WithApplication {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val result = legalNeighbours.calculate(validRequest)
      whenReady(result) { r =>
        r.body.map { b =>
          Json.parse(b) must equal(Seq.empty)
        }
      }
    }

    "call lookupNeighbours.fetch when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val lookupNeighbours = mock[LookupNeighbours]
      val injector = testInjector(new StubLookupNeighbours(lookupNeighbours))
      val sut = injector.getInstance(classOf[LegalNeighbours])

      val result = sut.calculate(validRequest)
      whenReady(result) { r =>
        verify(lookupNeighbours, times(1)).fetch(any[IScope], any[Int])
      }
    }

    "throw when submission contains unknown currentNode" in new WithApplication {
      val validRequest = requestWithDefaults(currentNode = 99)
      a[RuntimeException] must be thrownBy legalNeighbours.calculate(validRequest)
    }
  }

  private val legalNeighbours = testInjector().getInstance(classOf[LegalNeighbours])
  private val present = {
    val emptyRequest = FakeRequest()
    legalNeighbours.present(emptyRequest)
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
    val request = LegalNeighboursRequest(scope, currentNode)
    FakeRequest().withJsonBody(Json.toJson(request))
  }
}
