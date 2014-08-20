package controllers

import com.tzavellas.sse.guice.ScalaModule
import models.common.{IScope, LegalNeighboursRequest, Scope}
import nodes.NodeTreeFactoryImpl
import nodes.legalNeighbours.LegalNeighboursMemo
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import play.api.libs.json.Json
import play.api.test.Helpers.{BAD_REQUEST, OK}
import play.api.test.{FakeRequest, WithApplication}
import utils.helpers.UnitSpec
import scala.concurrent.ExecutionContext.Implicits.global

final class LegalNeighboursUnitSpec extends UnitSpec {

  "calculate" should {
    "return bad request when submission is empty" in new WithApplication {
      val emptyRequest = FakeRequest().withFormUrlEncodedBody()
      val result = legalNeighbours.calculate(emptyRequest)
      whenReady(result) { r =>
        r.header.status should equal(BAD_REQUEST)
      }
    }

    "return ok when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults()
      val result = legalNeighbours.calculate(validRequest)
      whenReady(result) { r =>
        r.header.status should equal(OK)
      }
    }

    "return ok with seq of ids when submission is valid" in new WithApplication {
      val validRequest = requestWithDefaults()
      val result = legalNeighbours.calculate(validRequest)
      whenReady(result) { r =>
        r.body.map { b =>
          Json.parse(b) should equal(Seq(NodeTreeFactoryImpl.id))
        }
      }
    }

    "return empty seq when submission is valid but no matches are in scope" in new WithApplication {
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))
      val result = legalNeighbours.calculate(validRequest)
      whenReady(result) { r =>
        r.body.map { b =>
          Json.parse(b) should equal(Seq.empty)
        }
      }
    }

    "call LegalNeighboursMemo.fetch when submission is valid" in new WithApplication {
      val legalNeighboursMemo = mock[LegalNeighboursMemo]
      val validRequest = requestWithDefaults(scopeDefault.copy(height = 0))

      final class StubLegalNeighboursMemo extends ScalaModule {

        def configure(): Unit = {
          when(legalNeighboursMemo.fetch(any[IScope], any[Int])).thenReturn(Seq.empty)
          bind(classOf[LegalNeighboursMemo]).toInstance(legalNeighboursMemo)
        }
      }

      val injector = testInjector(new StubLegalNeighboursMemo)
      val sut = injector.getInstance(classOf[LegalNeighbours])

      val result = sut.calculate(validRequest)
      whenReady(result) { r =>
        verify(legalNeighboursMemo, times(1)).fetch(any[IScope], any[Int])
      }
    }

    "throw when submission contains unknown currentNode" in pending
    "return empty result when submission is valid but no legal moves are found" in pending
    "return expected result when submission is valid and legal moves are found" in pending
  }

  private val legalNeighbours = injector.getInstance(classOf[LegalNeighbours])
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
