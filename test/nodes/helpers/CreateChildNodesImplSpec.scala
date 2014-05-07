package nodes.helpers

import org.specs2.mutable._
import nodes._
import org.specs2.mock.Mockito
import models.domain.scala.Empty
import models.domain.common.Node

class CreateChildNodesImplSpec extends Specification with Mockito {
  "CreateChildNodesImpl" should {
    trait MockUpdateScope extends UpdateScope {
      override def updateScope(scope: IScope): IScope = scope
    }

    case class mockFactoryNotTerminates1() extends CreateChildNodesImpl with MockUpdateScope {
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = false
      val neighbours: Seq[ICreateChildNodes] = Seq.empty
    }

    case class mockFactoryTerminates1() extends CreateChildNodesImpl with MockUpdateScope {
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = true
      val neighbours: Seq[ICreateChildNodes] = Seq.empty
    }

    case class mockFactoryTerminates2() extends CreateChildNodesImpl with MockUpdateScope {
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = true
      val neighbours: Seq[ICreateChildNodes] = Seq.empty
    }

    val nNot = mockFactoryNotTerminates1()
    val n1 = mockFactoryTerminates1()
    val n2 = mockFactoryTerminates2()

    case class TestCreateChildNodes() extends CreateChildNodesImpl with MockUpdateScope {
      val neighbours: Seq[ICreateChildNodes] = Seq(nNot,
        n1,
        nNot,
        n2)

      override def create(scope: IScope): Node = Empty()
    }

    case class TestCreateChildNodesImplNoValidChildren() extends CreateChildNodesImpl with MockUpdateScope {
      val neighbours: Seq[ICreateChildNodes] = Seq(mockFactoryNotTerminates1())
      override def create(scope: IScope): Node = Empty()
    }

    "updateScope returns unchanged by default" in {
      val scope = Scope(maxDepth = 10)
      val sut = TestCreateChildNodes()

      sut.updateScope(scope) mustEqual scope
    }
  }
}