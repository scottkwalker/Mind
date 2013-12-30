package nodes.helpers

import org.specs2.mutable._
import nodes._
import org.specs2.mock.Mockito

class CreateChildNodesSpec extends Specification with Mockito {
  "CreateChildNodes" should {
    trait MockUpdateScope extends UpdateScope {
      override def updateScope(scope: IScope): IScope = scope
    }
    case class mockFactoryNotTerminates1(memoizeCanTerminateInStepsRemaining: MemoizeDi = MemoizeDi()) extends ICreateChildNodes with MockUpdateScope {
      /*override val canTerminateInStepsRemaining: Scope => Boolean = {
        def inner(f: Scope => Boolean)(scope: IScope): Boolean = false
        Memoize.Y(inner)
      }*/
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = {
        def result = false
        memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
      }

      override def create(scope: IScope) = null

      val neighbours: Seq[ICreateChildNodes] = null
    }

    case class mockFactoryTerminates1(memoizeCanTerminateInStepsRemaining: MemoizeDi = MemoizeDi()) extends ICreateChildNodes with MockUpdateScope {
      /*override val canTerminateInStepsRemaining: Scope => Boolean = {
        def inner(f: Scope => Boolean)(scope: IScope): Boolean = true
        Memoize.Y(inner)
      }*/
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = {
        def result = true
        memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
      }

      override def create(scope: IScope) = null

      val neighbours: Seq[ICreateChildNodes] = null
    }

    case class mockFactoryTerminates2(memoizeCanTerminateInStepsRemaining: MemoizeDi = MemoizeDi()) extends ICreateChildNodes with MockUpdateScope {
      /*override val canTerminateInStepsRemaining: Scope => Boolean = {
        def inner(f: Scope => Boolean)(scope: IScope): Boolean = true
        Memoize.Y(inner)
      }*/
      override def canTerminateInStepsRemaining(scope: IScope): Boolean = {
        def result = true
        memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
      }

      override def create(scope: IScope) = null

      val neighbours: Seq[ICreateChildNodes] = null
    }

    val nNot = mockFactoryNotTerminates1()
    val n1 = mockFactoryTerminates1()
    val n2 = mockFactoryTerminates2()

    case class TestCreateChildNodes(memoizeCanTerminateInStepsRemaining: MemoizeDi = MemoizeDi()) extends ICreateChildNodes with MockUpdateScope {
      val neighbours: Seq[ICreateChildNodes] = Seq(nNot,
        n1,
        nNot,
        n2)

      override def create(scope: IScope): Node = Empty()
    }

    case class TestCreateChildNodesNoValidChildren(memoizeCanTerminateInStepsRemaining: MemoizeDi = MemoizeDi()) extends ICreateChildNodes with MockUpdateScope {
      val neighbours: Seq[ICreateChildNodes] = Seq(mockFactoryNotTerminates1())

      override def create(scope: IScope): Node = Empty()
    }

    "validChildren returns filtered seq" in {
      val scope = Scope(maxDepth = 10)
      val sut = TestCreateChildNodes()

      sut.legalNeighbours(scope) mustEqual Seq(n1, n2)
    }

    "updateScope returns unchanged by default" in {
      val scope = Scope(maxDepth = 10)
      val sut = TestCreateChildNodes()

      sut.updateScope(scope) mustEqual scope
    }
  }
}