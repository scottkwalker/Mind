package nodes.helpers

import ai.SelectionStrategy
import models.domain.common.Node
import org.mockito.Matchers._
import org.mockito.Mockito._
import utils.helpers.UnitSpec

final class CreateSeqNodeScalaTestSpec extends UnitSpec {

  "create" should {
    "call a factory once given only space for 1 func in obj and mocked rng the same" in {
      val expected = 1

      val s = mock[IScope]
      val n = mock[Node]
      val v = mock[ICreateChildNodes]
      val cn = mock[ICreateNode]
      val ai = mock[SelectionStrategy]

      val sut = CreateSeqNodes(cn, ai)

      when(s.maxFuncsInObject).thenReturn(1)
      when(ai.canAddAnother(anyInt(),
        anyInt())).thenReturn(true, false)

      when(cn.create(anyObject[Seq[ICreateChildNodes]],
        anyObject[IScope])).thenReturn((s, n))

      // Act
      val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
        scope = s,
        factoryLimit = s.maxFuncsInObject
      )

      // Assert
      assert(nodes.length == expected)
    }

    "calls create on factory twice given space for 2 func in obj and mocked rng the same" in {
      val expected = 2

      val s = mock[IScope]
      val n = mock[Node]
      val v = mock[ICreateChildNodes]
      val cn = mock[ICreateNode]
      val ai = mock[SelectionStrategy]

      val sut = CreateSeqNodes(cn, ai)
      when(s.maxFuncsInObject).thenReturn(2)
      when(ai.canAddAnother(anyInt(), anyInt())).thenReturn(true, true, false)
      when(cn.create(anyObject[Seq[ICreateChildNodes]], anyObject[IScope])).thenReturn((s, n))

      // Act
      val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
        scope = s,
        factoryLimit = s.maxFuncsInObject
      )

      // Assert
      assert(nodes.length == expected)
    }

    "calls create on factory once given space for 2 func in obj but rng mocked to 1" in {
      val expected = 1

      val s = mock[IScope]
      val n = mock[Node]
      val v = mock[ICreateChildNodes]
      val cn = mock[ICreateNode]
      val ai = mock[SelectionStrategy]

      val sut = CreateSeqNodes(cn, ai)

      when(s.maxFuncsInObject).thenReturn(2)
      when(ai.canAddAnother(anyInt(),
        anyInt())).thenReturn(true, false)

      when(cn.create(anyObject[Seq[ICreateChildNodes]],
        anyObject[IScope])).thenReturn((s, n))

      // Act
      val (_, nodes) = sut.createSeq(possibleChildren = Seq(v),
        scope = s,
        factoryLimit = s.maxFuncsInObject
      )

      // Assert
      assert(nodes.length == expected)
    }

    "calls create on factory once given space for 2 func in obj and a rng mocked to 2 but 1 pre-made node already added" in {
      val expected = 2

      val s = mock[IScope]
      val n = mock[Node]
      val v = mock[ICreateChildNodes]
      val cn = mock[ICreateNode]
      val ai = mock[SelectionStrategy]

      val sut = CreateSeqNodes(cn, ai)

      when(s.maxFuncsInObject).thenReturn(2)
      when(ai.canAddAnother(anyInt(), anyInt())).thenReturn(true, false)

      when(cn.create(anyObject[Seq[ICreateChildNodes]],
        anyObject[IScope])).thenReturn((s, n))

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
