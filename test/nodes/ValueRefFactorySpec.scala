package nodes

import org.specs2.mutable._
import nodes.helpers.{IScope, Scope}
import org.specs2.mock.Mockito
import com.google.inject.Injector
import com.google.inject.Guice
import modules.DevModule
import modules.ai.legalGamer.LegalGamerModule
import models.domain.scala.ValueRef

class ValueRefFactorySpec extends Specification with Mockito {
  val injector: Injector = Guice.createInjector(new DevModule, new LegalGamerModule)
  val factory = injector.getInstance(classOf[ValueRefFactory])

  "create" should {
    "returns instance of this type" in {
      val s = mock[IScope]
      s.numVals returns 1

      val instance = factory.create(scope = s)

      instance must beAnInstanceOf[ValueRef]
    }

    "returns expected given scope with 0 vals" in {
      val s = mock[IScope]
      s.numVals returns 1

      val instance = factory.create(scope = s)

      instance must beLike {
        case ValueRef(name) => name mustEqual "v0"
      }
    }

    "returns expected given scope with 1 val" in {
      val s = mock[IScope]
      s.numVals returns 1

      val instance = factory.create(scope = s)

      instance must beLike {
        case ValueRef(name) => name mustEqual "v0"
      }
    }
  }

  "neighbours" should {
    "be empty" in {
      factory.neighbours.length mustEqual 0
    }
  }

  "updateScope" should {
    "returns same" in {
      val s = mock[IScope]

      val s2 = factory.updateScope(s)

      s2 mustEqual s
    }
  }
}