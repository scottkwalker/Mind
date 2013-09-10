package nodes.helpers

import org.specs2.mutable._
import nodes._
import org.specs2.mock.Mockito
import ai.Ai

class CreateSeqNodesSpec extends Specification with Mockito {
  "CreateSeqNodes" should {
    "create" in {
      "calls create on factory" in {
        val s = mock[Scope]
        s.maxFuncsInObject returns 1
        val n = mock[Node]
        val v = mock[CreateChildNodes]
        v.updateScope(s) returns s
        v.create(any[Scope]) returns n 
        val ai = mock[Ai]
        ai.chooseChild(any[Seq[CreateChildNodes]], any[Scope]) returns v
        val cn = mock[CreateNode]
        cn.create(any[Seq[CreateChildNodes]], any[Scope], any[Ai]) returns ((s, n))
        val sut = CreateSeqNodes(cn)
        
        val (_, _) = sut.createSeq(Seq(v),
          s,
          ai,
          constraints = (s: Scope, accLength: Int) => accLength < s.maxFuncsInObject
        )
        
        there was one(cn).create(Seq(v), s, ai)
      }
    }
  }
}