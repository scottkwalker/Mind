package controllers

import composition.StubLookupChildrenBinding
import composition.StubSelectionStrategyBinding
import composition.TestComposition
import composition.UnitTestHelpers
import decision.TypeTreeFactory
import models.common.IScope
import models.common.LookupChildrenRequest
import models.common.Scope
import org.mockito.Matchers.any
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.PozInt

import scala.concurrent.ExecutionContext.Implicits.global

final class LegalChildrenUnitSpec extends UnitTestHelpers with TestComposition {

  "present" must {
    "return 200" in {
      whenReady(present) { r =>
        r.header.status must equal(OK)
      }(config = patienceConfig)
    }

    "contain a form that POSTs to the expected action" in {
      contentAsString(present)(timeout) must include( """form action="/mind/legal-children" method="POST"""")
    }
  }

  "calculate" must {
    "return bad request if given an empty submission" in {
      val emptyRequest = FakeRequest().withFormUrlEncodedBody()
      val (legalChildren, _) = build(size = 0)

      val calculate = legalChildren.calculate(emptyRequest)

      whenReady(calculate) {
        _.header.status must equal(BAD_REQUEST)
      }(config = patienceConfig)
    }

    "return ok if given a valid submission" in {
      val validRequest = requestWithDefaults()
      val (legalChildren, _) = build(size = 0)

      val calculate = legalChildren.calculate(validRequest)

      whenReady(calculate) {
        _.header.status must equal(OK)
      }(config = patienceConfig)
    }

    "call lookupChildren.calculate if given a valid submission" in {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val (legalChildren, lookupChildren) = build()

      val calculate = legalChildren.calculate(validRequest)

      whenReady(calculate) { r =>
        verify(lookupChildren, times(1)).get(any[IScope], any[PozInt])
        verifyNoMoreInteractions(lookupChildren)
      }(config = patienceConfig)
    }

    "return seq of ids if given a valid submission and legal moves are found" in {
      val validRequest = requestWithDefaults()
      val (legalChildren, _) = build(size = 0)
      val action = legalChildren.calculate(validRequest)
      whenReady(action) {
        _.body.map { b =>
          Json.parse(b) must equal(Seq(TypeTreeFactory.id))
        }
      }(config = patienceConfig)
    }

    "return empty seq if given a valid submission but no legal moves are in scope" in {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val (legalChildren, _) = build(size = 0)

      val calculate = legalChildren.calculate(validRequest)

      whenReady(calculate) {
        _.body.map { b =>
          Json.parse(b) must equal(Seq.empty)
        }
      }(config = patienceConfig)
    }
  }

  "size" must {
    "call lookupChildren.size once" in {
      val (legalChildren, lookupChildren) = build(size = 0)
      legalChildren.size(FakeRequest())
      verify(lookupChildren, times(1)).size
      verifyNoMoreInteractions(lookupChildren)
    }

    "return 0 if given an empty repository" in {
      val (legalChildren, _) = build(size = 0)
      val sizeOfRepository = legalChildren.size(FakeRequest())
      contentAsString(sizeOfRepository)(timeout) must equal("repository size: 0")
    }

    "return 1 if given a repository containing 1 item" in {
      val (legalChildren, _) = build(size = 1)
      val sizeOfRepository = legalChildren.size(FakeRequest())
      contentAsString(sizeOfRepository)(timeout) must equal("repository size: 1")
    }

    "return 3 if given a repository containing 3 items" in {
      val (legalChildren, _) = build(size = 3)
      val sizeOfRepository = legalChildren.size(FakeRequest())
      contentAsString(sizeOfRepository)(timeout) must equal("repository size: 3")
    }
  }

  private def present = {
    val emptyRequest = FakeRequest()
    val (legalChildren, _) = build(size = 0)
    legalChildren.present(emptyRequest)
  }

  private def build(size: Int = 0) = {
    val lookupChildren = new StubLookupChildrenBinding(size = size)
    val injector = testInjector(
      lookupChildren,
      new StubSelectionStrategyBinding
    )
    (injector.getInstance(classOf[LegalChildren]), lookupChildren.stub)
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
