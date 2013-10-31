package nodes.helpers

import org.specs2.mutable._
import nodes._
import org.specs2.mock.Mockito

class CreateChildNodesSpec extends Specification with Mockito {
  "CreateChildNodes" should {
    trait MockUpdateScope extends UpdateScope {
      override def updateScope(scope: Scope): Scope = scope
    }
    case class mockFactoryNotTerminates1(memoizeCanTerminateInStepsRemaining: MemoizeDi = MemoizeDi()) extends CreateChildNodes with MockUpdateScope {
      /*override val canTerminateInStepsRemaining: Scope => Boolean = {
        def inner(f: Scope => Boolean)(scope: Scope): Boolean = false
        Memoize.Y(inner)
      }*/
      override def canTerminateInStepsRemaining(scope: Scope): Boolean = {
        def result = false
        memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
      }

      override def create(scope: Scope) = null

      val neighbours: Seq[CreateChildNodes] = null
    }

    case class mockFactoryTerminates1(memoizeCanTerminateInStepsRemaining: MemoizeDi = MemoizeDi()) extends CreateChildNodes with MockUpdateScope {
      /*override val canTerminateInStepsRemaining: Scope => Boolean = {
        def inner(f: Scope => Boolean)(scope: Scope): Boolean = true
        Memoize.Y(inner)
      }*/
      override def canTerminateInStepsRemaining(scope: Scope): Boolean = {
        def result = true
        memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
      }

      override def create(scope: Scope) = null

      val neighbours: Seq[CreateChildNodes] = null
    }

    case class mockFactoryTerminates2(memoizeCanTerminateInStepsRemaining: MemoizeDi = MemoizeDi()) extends CreateChildNodes with MockUpdateScope {
      /*override val canTerminateInStepsRemaining: Scope => Boolean = {
        def inner(f: Scope => Boolean)(scope: Scope): Boolean = true
        Memoize.Y(inner)
      }*/
      override def canTerminateInStepsRemaining(scope: Scope): Boolean = {
        def result = true
        memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
      }

      override def create(scope: Scope) = null

      val neighbours: Seq[CreateChildNodes] = null
    }

    val nNot = mockFactoryNotTerminates1()
    val n1 = mockFactoryTerminates1()
    val n2 = mockFactoryTerminates2()

    case class TestCreateChildNodes(memoizeCanTerminateInStepsRemaining: MemoizeDi = MemoizeDi()) extends CreateChildNodes with MockUpdateScope {
      val neighbours: Seq[CreateChildNodes] = Seq(nNot,
        n1,
        nNot,
        n2)

      override def create(scope: Scope): Node = Empty()
    }

    case class TestCreateChildNodesNoValidChildren(memoizeCanTerminateInStepsRemaining: MemoizeDi = MemoizeDi()) extends CreateChildNodes with MockUpdateScope {
      val neighbours: Seq[CreateChildNodes] = Seq(mockFactoryNotTerminates1())

      override def create(scope: Scope): Node = Empty()
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