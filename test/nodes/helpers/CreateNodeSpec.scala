package nodes.helpers

import org.specs2.mutable._
import org.specs2.mock.Mockito
import ai.AiCommon

class CreateNodeSpec extends Specification with Mockito {
  "CreateNode" should {
    "create" in {
      "calls chooseChild on ai" in {
        val scope = Scope(maxDepth = 10)
        val v = mock[ICreateChildNodes]
        v.updateScope(scope) returns scope
        val ai = mock[AiCommon]
        ai.chooseChild(any[Seq[ICreateChildNodes]], any[Scope]) returns v
        val sut = CreateNode()

        val (_, _) = sut.create(Seq(v), scope, ai)

        there was one(ai).chooseChild(Seq(v), scope)
      }

      "calls updateScope" in {
        val scope = Scope(maxDepth = 10)
        val v = mock[ICreateChildNodes]
        v.updateScope(scope) returns scope
        val ai = mock[AiCommon]
        ai.chooseChild(any[Seq[ICreateChildNodes]], any[Scope]) returns v
        val sut = CreateNode()

        val (_, _) = sut.create(Seq(v), scope, ai)

        there was one(v).updateScope(scope)
      }

      "calls create on factory" in {
        val scope = Scope(maxDepth = 10)
        val v = mock[ICreateChildNodes]
        v.updateScope(scope) returns scope
        val ai = mock[AiCommon]
        ai.chooseChild(any[Seq[ICreateChildNodes]], any[Scope]) returns v
        val sut = CreateNode()

        val (_, _) = sut.create(Seq(v), scope, ai)

        there was one(v).create(scope)
      }
    }
  }
}