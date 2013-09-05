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
        s.objHasSpaceForChildren returns true thenReturns false
        s.incrementAccumulatorLength returns s
        val n = mock[Node]
        val v = mock[CreateChildNodes]
        v.updateScope(s) returns s
        v.create(any[Scope]) returns n 
        val ai = mock[Ai]
        ai.chooseChild(any[CreateChildNodes], any[Scope]) returns v
        val cn = mock[CreateNode]
        cn.create(any[CreateChildNodes], any[Scope], any[Ai]) returns ((s, n))
        def constraints(scope: Scope): Boolean = scope.objHasSpaceForChildren
        val sut = CreateSeqNodes(cn)
        
        val (_) = sut.create(v, s, ai, constraints, Seq())
        
        there was one(cn).create(v, s, ai)
      }
      
      "calls incrementAccumulatorLength" in {
        val s = mock[Scope]
        s.maxFuncsInObject returns 1
        s.objHasSpaceForChildren returns true thenReturns false
        s.incrementAccumulatorLength returns s
        val n = mock[Node]
        val v = mock[CreateChildNodes]
        v.updateScope(s) returns s
        v.create(any[Scope]) returns n 
        val ai = mock[Ai]
        ai.chooseChild(any[CreateChildNodes], any[Scope]) returns v
        val cn = mock[CreateNode]
        cn.create(any[CreateChildNodes], any[Scope], any[Ai]) returns ((s, n))
        def constraints(scope: Scope): Boolean = scope.objHasSpaceForChildren
        val sut = CreateSeqNodes(cn)
        
        val (_) = sut.create(v, s, ai, constraints, Seq())
        
        there was one(s).incrementAccumulatorLength
      }
    }
  }
}