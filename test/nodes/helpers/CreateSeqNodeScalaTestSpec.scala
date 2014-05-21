package nodes.helpers

import org.scalatest.WordSpec
import org.scalatest.mock.EasyMockSugar
import org.easymock.EasyMock._
import ai.IAi
import models.domain.common.Node

final class CreateSeqNodeScalaTestSpec extends WordSpec with EasyMockSugar {
  "create" should {
    "call a factory once given only space for 1 func in obj and mocked rng the same" in {
      val expected = 1

      val s = strictMock[IScope]
      val n = strictMock[Node]
      val v = strictMock[ICreateChildNodes]
      val cn = strictMock[ICreateNode]
      val ai = strictMock[IAi]

      val sut = CreateSeqNodes(cn, ai)

      expecting {
        s.maxFuncsInObject andReturn 1
        ai.canAddAnother(anyInt(),
          anyInt()) andReturn true andReturn false

        cn.create(anyObject[Seq[ICreateChildNodes]],
          anyObject[IScope],
          anyObject[IAi]) andReturn ((s, n))
      }

      whenExecuting(s, n, v, cn, ai) {
        // Act
        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          factoryLimit = s.maxFuncsInObject
        )

        // Assert
        assert(nodes.length == expected)
      }
    }

    "calls create on factory twice given space for 2 func in obj and mocked rng the same" in {
      val expected = 2

      val s = strictMock[IScope]
      val n = strictMock[Node]
      val v = strictMock[ICreateChildNodes]
      val cn = strictMock[ICreateNode]
      val ai = strictMock[IAi]

      val sut = CreateSeqNodes(cn, ai)

      expecting {
        s.maxFuncsInObject andReturn 2
        ai.canAddAnother(anyInt(),
          anyInt()) andReturn true andReturn true andReturn false

        (cn.create(anyObject[Seq[ICreateChildNodes]],
          anyObject[IScope],
          anyObject[IAi]) andReturn ((s, n))).
          times(2)
      }

      whenExecuting(s, n, v, cn, ai) {
        // Act
        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          factoryLimit = s.maxFuncsInObject
        )

        // Assert
        assert(nodes.length == expected)
      }
    }

    "calls create on factory once given space for 2 func in obj but rng mocked to 1" in {
      val expected = 1

      val s = strictMock[IScope]
      val n = strictMock[Node]
      val v = strictMock[ICreateChildNodes]
      val cn = strictMock[ICreateNode]
      val ai = strictMock[IAi]

      val sut = CreateSeqNodes(cn, ai)

      expecting {
        s.maxFuncsInObject andReturn 2
        ai.canAddAnother(anyInt(),
          anyInt()) andReturn true andReturn false

        (cn.create(anyObject[Seq[ICreateChildNodes]],
          anyObject[IScope],
          anyObject[IAi]) andReturn ((s, n))).
          once
      }

      whenExecuting(s, n, v, cn, ai) {
        // Act
        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          factoryLimit = s.maxFuncsInObject
        )

        // Assert
        assert(nodes.length == expected)
      }
    }

    "calls create on factory once given space for 2 func in obj and a rng mocked to 2 but 1 pre-made node already added" in {
      val expected = 2

      val s = strictMock[IScope]
      val n = strictMock[Node]
      val v = strictMock[ICreateChildNodes]
      val cn = strictMock[ICreateNode]
      val ai = strictMock[IAi]

      val sut = CreateSeqNodes(cn, ai)

      expecting {
        s.maxFuncsInObject andReturn 2
        ai.canAddAnother(anyInt(),
          anyInt()) andReturn true andReturn false

        (cn.create(anyObject[Seq[ICreateChildNodes]],
          anyObject[IScope],
          anyObject[IAi]) andReturn ((s, n))).
          once
      }

      whenExecuting(s, n, v, cn, ai) {
        // Act
        val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
          scope = s,
          acc = Seq(n), // Pre-made
          factoryLimit = s.maxFuncsInObject
        )

        // Assert
        assert(nodes.length == expected)
      }
    }
  }
}
