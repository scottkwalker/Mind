package controllers

import composition.{StubLookupChildrenBinding, StubLookupChildrenWithFutures, TestComposition}
import memoization.{LookupChildren, LookupChildrenWithFutures}
import models.common.{IScope, LookupChildrenRequest, Scope}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify}
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, WithApplication}
import replaceEmpty.TypeTreeFactoryImpl
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global

final class LegalChildrenUnitSpec extends TestComposition {

  "present" must {
    "return 200" in new WithApplication {
      whenReady(present) { r =>
        r.header.status must equal(OK)
      }(config = patienceConfig)
    }

    "contain a form that POSTs to the expected action" in new WithApplication {
      contentAsString(present)(timeout) must include( """form action="/mind/legal-children" method="POST"""")
    }
  }

  "calculate" must {
    "return bad request when submission is empty" in new WithApplication {
      val emptyRequest = FakeRequest().withFormUrlEncodedBody()
      val (legalChildren, _) = build(size = 0)
      val result = legalChildren.calculate(emptyRequest)
      whenReady(result) { r =>
        r.header.status must equal(BAD_REQUEST)
      }(config = patienceConfig)
    }

    "return ok when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults()
      val (legalChildren, _) = build(size = 0)
      val result = legalChildren.calculate(validRequest)
      whenReady(result) { r =>
        r.header.status must equal(OK)
      }(config = patienceConfig)
    }

    "return seq of ids when submission is valid and legal moves are found" in new WithApplication {
      val validRequest = requestWithDefaults()
      val (legalChildren, _) = build(size = 0)
      val result = legalChildren.calculate(validRequest)
      whenReady(result) { r =>
        r.body.map { b =>
          Json.parse(b) must equal(Seq(TypeTreeFactoryImpl.id))
        }
      }(config = patienceConfig)
    }

    "return empty seq when submission is valid but no matches are in scope" in new WithApplication {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val (legalChildren, _) = build(size = 0)
      val result = legalChildren.calculate(validRequest)
      whenReady(result) { r =>
        r.body.map { b =>
          Json.parse(b) must equal(Seq.empty)
        }
      }(config = patienceConfig)
    }

    "call lookupChildren.calculate when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val lookupChildren = new StubLookupChildrenBinding
      val injector = testInjector(
        lookupChildren
      )
      val sut = injector.getInstance(classOf[LegalChildren])

      val result = sut.calculate(validRequest)
      whenReady(result) { r =>
        verify(lookupChildren.stub, times(1)).get(any[IScope], any[PozInt])
      }(config = patienceConfig)
    }
  }

  "size" must {
    "call lookupChildren.size" in {
      val (legalChildren, lookupChildren) = build(size = 0)
      legalChildren.size(FakeRequest())
      verify(lookupChildren, times(1)).size
    }

    "return 0 when repository is empty" in {
      val (legalChildren, _) = build(size = 0)
      val result = legalChildren.size(FakeRequest())
      contentAsString(result)(timeout) must equal("repository size: 0")
    }

    "return 1 when repository has 1 item" in {
      val (legalChildren, _) = build(size = 1)
      val result = legalChildren.size(FakeRequest())
      contentAsString(result)(timeout) must equal("repository size: 1")
    }
    
    "return 3 when repository has 3 items" in {
      val (legalChildren, _) = build(size = 3)
      val result = legalChildren.size(FakeRequest())
      contentAsString(result)(timeout) must equal("repository size: 3")
    }
  }

  private def build(size: Int = 0) = {
    val lookupChildren= new StubLookupChildrenBinding(size = size)
    val injector = testInjector(
      lookupChildren
    )
    (injector.getInstance(classOf[LegalChildren]), lookupChildren.stub)
  }

  private def present = {
    val emptyRequest = FakeRequest()
    val (legalChildren, _) = build(size = 0)
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
