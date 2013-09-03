package nodes.helpers

import org.specs2.mutable._
import nodes._
import org.specs2.mock.Mockito
import ai.helpers.TestAi
import ai.Ai

class CreateNodeSpec extends Specification with Mockito {
  "CreateNode" should {
    "create" in {
      "calls chooseChild on ai" in {
        val scope = Scope(stepsRemaining = 10)
        val v = mock[CreateChildNodes]
        v.updateScope(scope) returns scope
        val ai = mock[Ai]
        ai.chooseChild(any[CreateChildNodes], any[Scope]) returns v
        val sut = CreateNode()
        
        val (updatedScope, child) = sut.create(v, scope, ai)
        
        there was one(ai).chooseChild(v, scope)
      }

      "calls updateScope" in {
        val scope = Scope(stepsRemaining = 10)
        val v = mock[CreateChildNodes]
        v.updateScope(scope) returns scope
        val ai = mock[Ai]
        ai.chooseChild(any[CreateChildNodes], any[Scope]) returns v
        val sut = CreateNode()
        
        val (updatedScope, child) = sut.create(v, scope, ai)
        
        there was one(v).updateScope(scope)
      }

      "calls create on factory" in {
        val scope = Scope(stepsRemaining = 10)
        val v = mock[CreateChildNodes]
        v.updateScope(scope) returns scope
        val ai = mock[Ai]
        ai.chooseChild(any[CreateChildNodes], any[Scope]) returns v
        val sut = CreateNode()
        
        val (updatedScope, child) = sut.create(v, scope, ai)
        
        there was one(v).create(scope)
      }
    }
  }
}