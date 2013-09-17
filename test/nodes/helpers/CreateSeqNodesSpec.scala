package nodes.helpers

import org.specs2.mutable._
import nodes._
import org.specs2.mock.Mockito
import ai.Ai
import scala.util.Random

class CreateSeqNodesSpec extends Specification with Mockito {
  "CreateSeqNodes" should {
    "create" in {
      "calls create on factory once given only space for 1 func in obj and mocked rng the same" in {
        val s = mock[Scope]
        s.maxFuncsInObject returns 1
        val n = mock[Node]
        val v = mock[CreateChildNodes]
        v.updateScope(s) returns s
        v.create(any[Scope], any[Option[Node]]) returns n
        val ai = mock[Ai]
        ai.chooseChild(any[Seq[CreateChildNodes]], any[Scope]) returns v
        val cn = mock[CreateNode]
        cn.create(any[Seq[CreateChildNodes]], any[Scope], any[Ai], any[Option[Node]]) returns ((s, n))
        val sut = CreateSeqNodes(cn)
        
        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          ai = ai,
          constraints = (s: Scope, accLength: Int) => accLength < s.maxFuncsInObject,
          premade = None
        )
        
        there was one(cn).create(Seq(v), s, ai, None)
        nodes.length must equalTo(1)
      }

      "calls create on factory twice given space for 2 func in obj and mocked rng the same" in {
        val s = mock[Scope]
        s.maxFuncsInObject returns 2
        val n = mock[Node]
        val v = mock[CreateChildNodes]
        v.updateScope(s) returns s
        v.create(any[Scope], any[Option[Node]]) returns n
        val ai = mock[Ai]
        ai.chooseChild(any[Seq[CreateChildNodes]], any[Scope]) returns v
        val cn = mock[CreateNode]
        cn.create(any[Seq[CreateChildNodes]], any[Scope], any[Ai], any[Option[Node]]) returns ((s, n))
        val rng = mock[Random]
        rng.nextInt(any[Int]) returns 2
        val sut = CreateSeqNodes(cn)

        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          ai = ai,
          constraints = (s: Scope, accLength: Int) => accLength < rng.nextInt(s.maxFuncsInObject),
          premade = None
        )

        there was two(cn).create(Seq(v), s, ai, premade = None)
        nodes.length must equalTo(2)
      }

      "calls create on factory once given space for 2 func in obj but rng mocked to 1" in {
        val s = mock[Scope]
        s.maxFuncsInObject returns 2
        val n = mock[Node]
        val v = mock[CreateChildNodes]
        v.updateScope(s) returns s
        v.create(any[Scope], any[Option[Node]]) returns n
        val ai = mock[Ai]
        ai.chooseChild(any[Seq[CreateChildNodes]], any[Scope]) returns v
        val cn = mock[CreateNode]
        cn.create(any[Seq[CreateChildNodes]], any[Scope], any[Ai], any[Option[Node]]) returns ((s, n))
        val rng = mock[Random]
        rng.nextInt(any[Int]) returns 1
        val sut = CreateSeqNodes(cn)

        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          ai = ai,
          constraints = (s: Scope, accLength: Int) => accLength < rng.nextInt(s.maxFuncsInObject),
          premade = None
        )

        there was one(cn).create(Seq(v), s, ai, premade = None)
        nodes.length must equalTo(1)
      }

      "calls create on factory once given space for 2 func in obj and a rng mocked to 2 but 1 pre-made node already added" in {
        val s = mock[Scope]
        s.maxFuncsInObject returns 2
        val n = mock[Node]
        val v = mock[CreateChildNodes]
        v.updateScope(s) returns s
        v.create(any[Scope], any[Option[Node]]) returns n
        val ai = mock[Ai]
        ai.chooseChild(any[Seq[CreateChildNodes]], any[Scope]) returns v
        val cn = mock[CreateNode]
        cn.create(any[Seq[CreateChildNodes]], any[Scope], any[Ai], any[Option[Node]]) returns ((s, n))
        val rng = mock[Random]
        rng.nextInt(any[Int]) returns 2
        val sut = CreateSeqNodes(cn)

        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          ai = ai,
          constraints = (s: Scope, accLength: Int) => accLength < rng.nextInt(s.maxFuncsInObject),
          acc = Seq(n),
          premade = None
        )

        there was one(cn).create(Seq(v), s, ai, premade = None)
        nodes.length must equalTo(2)
      }
    }
  }
}