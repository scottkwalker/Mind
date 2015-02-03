package replaceEmpty

import ai.RandomNumberGenerator
import composition.{StubCreateSeqNodesBinding, StubIScopeBinding, StubRngBinding, TestComposition}
import models.common.{IScope, Scope}
import models.domain.Instruction
import models.domain.scala.Object
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class ObjectFactorySpec extends TestComposition {

  "create" must {
    "returns instance of this type" in {
      val (objectFactory, scope, _) = build()

      val result = objectFactory.create(scope = scope)

      whenReady(result) { result =>
        result mustBe an[Object]
      }(config = patienceConfig)
    }

    "returns expected given scope with 0 existing objects" in {
      val (objectFactory, scope, _) = build(numObjects = 0)

      val result = objectFactory.create(scope = scope)

      whenReady(result) {
        case Object(_, name) => name must equal("o0")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "returns expected given scope with 1 existing objects" in {
      val (objectFactory, scope, _) = build(numObjects = 1)

      val result = objectFactory.create(scope = scope)

      whenReady(result) {
        case Object(_, name) => name must equal("o1")
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "calls CreateSeqNodes.create once" in {
      val (objectFactory, scope, createSeqNodes) = build(numObjects = 1)

      val result = objectFactory.create(scope = scope)

      whenReady(result) { r =>
        verify(createSeqNodes, times(1)).create(any[Future[Set[ReplaceEmpty]]], any[IScope], any[Seq[Instruction]], any[Int])
      }(config = patienceConfig)
    }
  }

  "updateScope" must {
    "call increment objects" in {
      val scope = mock[IScope]
      val (objectFactory, _, _) = build()

      objectFactory.updateScope(scope)

      verify(scope, times(1)).incrementObjects
    }
  }

  private def build(nextInt: Int = 0, numObjects: Int = 1) = {
    val createSeqNodes = new StubCreateSeqNodesBinding
    val rng: RandomNumberGenerator = mock[RandomNumberGenerator]
    when(rng.nextInt(any[Int])).thenReturn(nextInt)
    val injector = testInjector(
      new StubRngBinding(randomNumberGenerator = rng),
      new StubIScopeBinding(numObjects = numObjects),
      createSeqNodes
    )
    (injector.getInstance(classOf[ObjectFactoryImpl]), injector.getInstance(classOf[IScope]), createSeqNodes.stub)
  }
}