package nodes.helpers

import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar
import nodes.Node
import scala.util.Random
import ai.Ai
import ai.aco.Aco

class CreateSeqNodeScalaTestSpec  extends WordSpec with EasyMockSugar {
  "create" should {
/*
    "call a factory once given only space for 1 func in obj and mocked rng the same" in {

      val s = strictMock[IScope]
      val n = strictMock[Node]
      val v = mock[CreateChildNodes]

      expecting {
        s.maxFuncsInObject.andReturn(1)
        v.updateScope(s).andReturn(s)

      }

      whenExecuting(s) {
        // Act


        // Assert

      }


      /*


      v.create(any[Scope]) returns n
      val rng = mock[Random]
      rng.nextBoolean() returns true
      val ai: Ai = Aco(rng)
      val cn = mock[CreateNode]
      cn.create(any[Seq[CreateChildNodes]], any[Scope], any[Ai]) returns ((s, n))
      val sut = CreateSeqNodes(cn, rng, ai)

      val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
        scope = s,
        factoryLimit = s.maxFuncsInObject
      )

      there was one(cn).create(Seq(v), s, ai)
      nodes.length must equalTo(1)
      */
    }*/
  }
}
