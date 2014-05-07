package nodes.helpers

import org.specs2.mutable._
import nodes._
import org.specs2.mock.Mockito
import models.domain.scala.Empty
import models.domain.common.Node

class CreateChildNodesSpec extends Specification with Mockito {
  "CreateChildNodes" should {
    trait MockUpdateScope extends UpdateScope {
      override def updateScope(scope: IScope): IScope = scope
    }

    case class mockFactoryNotTerminates1() extends ICreateChildNodes with MockUpdateScope {
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = false
      val neighbours: Seq[ICreateChildNodes] = Seq.empty
    }

    case class mockFactoryTerminates1() extends ICreateChildNodes with MockUpdateScope {
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = true
      val neighbours: Seq[ICreateChildNodes] = Seq.empty
    }

    case class mockFactoryTerminates2() extends ICreateChildNodes with MockUpdateScope {
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = true
      val neighbours: Seq[ICreateChildNodes] = Seq.empty
    }

    val nNot = mockFactoryNotTerminates1()
    val n1 = mockFactoryTerminates1()
    val n2 = mockFactoryTerminates2()

    case class TestCreateChildNodes() extends ICreateChildNodes with MockUpdateScope {
      val neighbours: Seq[ICreateChildNodes] = Seq(nNot,
        n1,
        nNot,
        n2)

      override def create(scope: IScope): Node = Empty()
    }

    case class TestCreateChildNodesNoValidChildren() extends ICreateChildNodes with MockUpdateScope {
      val neighbours: Seq[ICreateChildNodes] = Seq(mockFactoryNotTerminates1())
      override def create(scope: IScope): Node = Empty()
    }

    "validChildren returns filtered seq" in {
      val scope = Scope(maxDepth = 10)
      val sut = TestCreateChildNodes()

      sut.legalNeighbours(scope, sut.neighbours) mustEqual Seq(n1, n2)
    }

    "updateScope returns unchanged by default" in {
      val scope = Scope(maxDepth = 10)
      val sut = TestCreateChildNodes()

      sut.updateScope(scope) mustEqual scope
    }
  }
}