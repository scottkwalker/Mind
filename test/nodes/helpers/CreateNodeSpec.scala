package nodes.helpers

import org.specs2.mutable._
import org.specs2.mock.Mockito
import ai.Ai

class CreateNodeSpec extends Specification with Mockito {
  "CreateNode" should {
    "create" in {
      "calls chooseChild on ai" in {
        val scope = Scope(maxDepth = 10)
        val v = mock[CreateChildNodes]
        v.updateScope(scope) returns scope
        val ai = mock[Ai]
        ai.chooseChild(any[Seq[CreateChildNodes]], any[Scope]) returns v
        val sut = CreateNode()

        val (_, _) = sut.create(Seq(v), scope, ai)

        there was one(ai).chooseChild(Seq(v), scope)
      }

      "calls updateScope" in {
        val scope = Scope(maxDepth = 10)
        val v = mock[CreateChildNodes]
        v.updateScope(scope) returns scope
        val ai = mock[Ai]
        ai.chooseChild(any[Seq[CreateChildNodes]], any[Scope]) returns v
        val sut = CreateNode()

        val (_, _) = sut.create(Seq(v), scope, ai)

        there was one(v).updateScope(scope)
      }

      "calls create on factory" in {
        val scope = Scope(maxDepth = 10)
        val v = mock[CreateChildNodes]
        v.updateScope(scope) returns scope
        val ai = mock[Ai]
        ai.chooseChild(any[Seq[CreateChildNodes]], any[Scope]) returns v
        val sut = CreateNode()

        val (_, _) = sut.create(Seq(v), scope, ai)

        there was one(v).create(scope)
      }
    }
  }
}