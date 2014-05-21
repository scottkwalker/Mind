package nodes.helpers

import ai.{IRandomNumberGenerator, SelectionStrategy}
import ai.aco.Aco
import models.domain.common.Node
import utils.helpers.UnitSpec
import org.mockito.Mockito._
import org.mockito.Matchers.any

final class CreateSeqNodesSpec extends UnitSpec {
  "createSeq" should {
    "calls create on factory once given only space for 1 func in obj and mocked rng the same" in {
      val s = mock[IScope]
      when(s.maxFuncsInObject).thenReturn(1)
      val n = mock[Node]
      val v = mock[ICreateChildNodes]
      when(v.updateScope(s) ).thenReturn( s)
        when(v.create(any[Scope]) ).thenReturn( n)
      val rng = mock[IRandomNumberGenerator]
      when(rng.nextBoolean() ).thenReturn( true)
      val ai: SelectionStrategy = Aco(rng)
      val cn = mock[ICreateNode]
      when(cn.create(any[Seq[ICreateChildNodes]], any[Scope], any[SelectionStrategy]) ).thenReturn ((s, n))
      val sut = CreateSeqNodes(cn, ai)

      val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
        scope = s,
        factoryLimit = s.maxFuncsInObject
      )

      verify(cn, times(1)).create(Seq(v), s, ai)
      nodes.length should equal(1)
    }

    "calls create on factory twice given space for 2 func in obj and mocked rng the same" in {
      val s = mock[IScope]
      when(s.maxFuncsInObject ).thenReturn( 2)
      val n = mock[Node]
      val v = mock[ICreateChildNodes]
      when(v.updateScope(s) ).thenReturn( s)
      when(v.create(any[Scope]) ).thenReturn( n)
      val cn = mock[ICreateNode]
      when(cn.create(any[Seq[ICreateChildNodes]], any[Scope], any[SelectionStrategy]) ).thenReturn ((s, n))
      val rng = mock[IRandomNumberGenerator]
      when(rng.nextInt(any[Int]) ).thenReturn( 2)
      when(rng.nextBoolean() ).thenReturn( true)
      val ai: SelectionStrategy = Aco(rng)
      val sut = CreateSeqNodes(cn, ai)

      val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
        scope = s,
        factoryLimit = s.maxFuncsInObject
      )

      verify(cn, times(2)).create(Seq(v), s, ai)
      nodes.length should equal(2)
    }

    "calls create on factory once given space for 2 func in obj but rng mocked to 1" in {
      val s = mock[IScope]
      when(s.maxFuncsInObject).thenReturn( 2)
      val n = mock[Node]
      val v = mock[ICreateChildNodes]
      when(v.updateScope(s) ).thenReturn( s)
      when(v.create(any[Scope]) ).thenReturn( n)
      val cn = mock[ICreateNode]
      when(cn.create(any[Seq[ICreateChildNodes]], any[Scope], any[SelectionStrategy]) ).thenReturn ((s, n))
      val rng = mock[IRandomNumberGenerator]
      when(rng.nextInt(any[Int]) ).thenReturn( 1)
      when(rng.nextBoolean() ).thenReturn(false, true)
      val ai: SelectionStrategy = Aco(rng)
      val sut = CreateSeqNodes(cn, ai)

      val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
        scope = s,
        factoryLimit = s.maxFuncsInObject
      )

      verify(cn, times(1)).create(Seq(v), s, ai)
      nodes.length should equal(1)
    }

    "calls create on factory once given space for 2 func in obj and a rng mocked to 2 but 1 pre-made node already added" in {
      val s = mock[IScope]
      when(s.maxFuncsInObject ).thenReturn( 2)
      val n = mock[Node]
      val v = mock[ICreateChildNodes]
      when(v.updateScope(s) ).thenReturn( s)
      when(v.create(any[Scope]) ).thenReturn( n)
      val cn = mock[ICreateNode]
      when(cn.create(any[Seq[ICreateChildNodes]], any[Scope], any[SelectionStrategy]) ).thenReturn ((s, n))
      val rng = mock[IRandomNumberGenerator]
      when(rng.nextInt(any[Int]) ).thenReturn( 2)
      when(rng.nextBoolean() ).thenReturn( true)
      val ai: SelectionStrategy = Aco(rng)
      val sut = CreateSeqNodes(cn, ai)

      val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
        scope = s,
        acc = Seq(n),
        factoryLimit = s.maxFuncsInObject
      )

      verify(cn, times(1)).create(Seq(v), s, ai)
      nodes.length should equal(2)
    }
  }
}