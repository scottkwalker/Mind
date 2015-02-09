package decision

import composition.StubCreateSeqNodesBinding
import composition.StubRngBinding
import composition.TestComposition
import models.common.IScope
import models.common.Scope
import models.domain.Step
import models.domain.scala.TypeTree
import org.mockito.Matchers._
import org.mockito.Mockito._

import scala.concurrent.Future

final class TypeTreeFactorySpec extends TestComposition {

  "create step" must {
    "returns instance of this type" in {
      val (typeTreeFactory, _) = build()

      val result = typeTreeFactory.createStep(Scope())

      whenReady(result) { result =>
        result mustBe a[TypeTree]
      }(config = patienceConfig)
    }

    "calls CreateSeqNodes.create" in {
      val (typeTreeFactory, createSeqNodes) = build()

      val result = typeTreeFactory.create(Scope(), Seq.empty)

      whenReady(result) { r =>
        verify(createSeqNodes, times(1)).create(any[Future[Set[Decision]]], any[IScope], any[Seq[Step]], any[Int])
      }(config = patienceConfig)
    }

    "returns 2 children given 1 premade and 1 generated (by mocked dependency)" in {
      val premadeNode = mock[Step]
      val premadeChildren = {
        val decision = mock[Decision]
        when(decision.createStep(any[IScope])).thenReturn(Future.successful(premadeNode))
        Seq(decision)
      }
      val (typeTreeFactory, _) = build(nextInt = 3)

      val result = typeTreeFactory.create(scope = Scope(), premadeChildren = premadeChildren)

      whenReady(result) {
        case TypeTree(child) =>
          child.length must equal(2) // 1 generated and 1 premade
          child.last must equal(premadeNode) // The premade are concatenated to the end of the seq
        case _ => fail("wrong type")
      }(config = patienceConfig)
    }

    "throw if you ask updateScope" in {
      val scope = mock[IScope]
      val (typeTreeFactory, _) = build()
      a[RuntimeException] must be thrownBy typeTreeFactory.updateScope(scope)
    }
  }

  private def build(nextInt: Int = 0) = {
    val randomNumberGenerator = new StubRngBinding(nextInt = nextInt)
    val createSeqNodes = new StubCreateSeqNodesBinding
    val injector = testInjector(
      randomNumberGenerator,
      createSeqNodes
    )
    (injector.getInstance(classOf[TypeTreeFactory]), createSeqNodes.stub)
  }
}