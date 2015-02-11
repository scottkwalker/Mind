package decision

import composition.StubCreateSeqNodesBinding
import composition.StubRngBinding
import composition.TestComposition
import models.common.IScope
import models.common.Scope
import models.domain.Step
import models.domain.scala.Object
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class ObjectFactorySpec extends TestComposition {

  "create step" must {
    "returns instance of this type" in {
      val (objectFactory, _) = build()

      val step = objectFactory.createStep(scope = Scope())

      whenReady(step) { result =>
        result mustBe an[Object]
      }(config = patienceConfig)
    }

    "returns expected given scope with 0 existing objects" in {
      val (objectFactory, _) = build()

      val step = objectFactory.createStep(scope = Scope())

      whenReady(step) {
        case Object(_, name) => name must equal("o0")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "returns expected given scope with 1 existing objects" in {
      val (objectFactory, _) = build()

      val step = objectFactory.createStep(scope = Scope(numObjects = 1))

      whenReady(step) {
        case Object(_, name) => name must equal("o1")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "calls CreateSeqNodes.create once" in {
      val (objectFactory, createSeqNodes) = build()

      val step = objectFactory.createStep(scope = Scope())

      whenReady(step) { r =>
        verify(createSeqNodes, times(1)).create(any[Future[Set[Decision]]], any[IScope], any[Seq[Step]], any[Int])
      }(config = patienceConfig)
    }
  }

  "updateScope" must {
    "call increment objects" in {
      val scope = mock[IScope]
      val (objectFactory, _) = build()

      objectFactory.updateScope(scope)

      verify(scope, times(1)).incrementObjects
    }
  }

  private def build(nextInt: Int = 0) = {
    val randomNumberGenerator = new StubRngBinding(nextInt = nextInt)
    val createSeqNodes = new StubCreateSeqNodesBinding
    val injector = testInjector(
      randomNumberGenerator,
      createSeqNodes
    )
    (injector.getInstance(classOf[ObjectFactory]), createSeqNodes.stub)
  }
}