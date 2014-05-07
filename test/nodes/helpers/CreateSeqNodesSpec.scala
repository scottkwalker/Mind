package nodes.helpers

import org.specs2.mutable._
import nodes._
import org.specs2.mock.Mockito
import ai.{IRandomNumberGenerator, SelectionStrategy}
import ai.aco.Aco
import models.domain.common.Node

class CreateSeqNodesSpec extends Specification with Mockito {
  "CreateSeqNodes" should {
    "create" in {
      "calls create on factory once given only space for 1 func in obj and mocked rng the same" in {
        val s = mock[Scope]
        s.maxFuncsInObject returns 1
        val n = mock[Node]
        val v = mock[ICreateChildNodes]
        v.updateScope(s) returns s
        v.create(any[Scope]) returns n
        val rng = mock[IRandomNumberGenerator]
        rng.nextBoolean() returns true
        val ai: SelectionStrategy = Aco(rng)
        val cn = mock[CreateNode]
        cn.create(any[Seq[ICreateChildNodes]], any[Scope], any[SelectionStrategy]) returns ((s, n))
        val sut = CreateSeqNodes(cn, rng, ai)

        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          factoryLimit = s.maxFuncsInObject
        )

        there was one(cn).create(Seq(v), s, ai)
        nodes.length must equalTo(1)
      }

      "calls create on factory twice given space for 2 func in obj and mocked rng the same" in {
        val s = mock[Scope]
        s.maxFuncsInObject returns 2
        val n = mock[Node]
        val v = mock[ICreateChildNodes]
        v.updateScope(s) returns s
        v.create(any[Scope]) returns n
        val cn = mock[CreateNode]
        cn.create(any[Seq[ICreateChildNodes]], any[Scope], any[SelectionStrategy]) returns ((s, n))
        val rng = mock[IRandomNumberGenerator]
        rng.nextInt(any[Int]) returns 2
        rng.nextBoolean() returns true
        val ai: SelectionStrategy = Aco(rng)
        val sut = CreateSeqNodes(cn, rng, ai)

        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          factoryLimit = s.maxFuncsInObject
        )

        there was two(cn).create(Seq(v), s, ai)
        nodes.length must equalTo(2)
      }

      "calls create on factory once given space for 2 func in obj but rng mocked to 1" in {
        val s = mock[Scope]
        s.maxFuncsInObject returns 2
        val n = mock[Node]
        val v = mock[ICreateChildNodes]
        v.updateScope(s) returns s
        v.create(any[Scope]) returns n
        val cn = mock[CreateNode]
        cn.create(any[Seq[ICreateChildNodes]], any[Scope], any[SelectionStrategy]) returns ((s, n))
        val rng = mock[IRandomNumberGenerator]
        rng.nextInt(any[Int]) returns 1
        rng.nextBoolean() returns true
        val ai: SelectionStrategy = Aco(rng)
        val sut = CreateSeqNodes(cn, rng, ai)

        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          factoryLimit = s.maxFuncsInObject
        )

        there was one(cn).create(Seq(v), s, ai)
        nodes.length must equalTo(1)
      }.pendingUntilFixed("Need to get mockito working to return false then return true")

      "calls create on factory once given space for 2 func in obj and a rng mocked to 2 but 1 pre-made node already added" in {
        val s = mock[Scope]
        s.maxFuncsInObject returns 2
        val n = mock[Node]
        val v = mock[ICreateChildNodes]
        v.updateScope(s) returns s
        v.create(any[Scope]) returns n
        val cn = mock[CreateNode]
        cn.create(any[Seq[ICreateChildNodes]], any[Scope], any[SelectionStrategy]) returns ((s, n))
        val rng = mock[IRandomNumberGenerator]
        rng.nextInt(any[Int]) returns 2
        rng.nextBoolean() returns true
        val ai: SelectionStrategy = Aco(rng)
        val sut = CreateSeqNodes(cn, rng, ai)

        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          acc = Seq(n),
          factoryLimit = s.maxFuncsInObject
        )

        there was one(cn).create(Seq(v), s, ai)
        nodes.length must equalTo(2)
      }
    }
  }
}