package composition

import _root_.ai.{RandomNumberGenerator, RandomNumberGeneratorImpl}
import com.google.inject.{AbstractModule, TypeLiteral}
import memoization._
import models.common.{IScope, Scope}
import models.domain.scala.{Empty, FactoryLookup, FactoryLookupImpl}
import replaceEmpty._
import utils.PozInt

import scala.concurrent.Future

final class CreateSeqNodesBinding extends AbstractModule {

  override def configure(): Unit = bind(classOf[CreateSeqNodes]).to(classOf[CreateSeqNodesImpl]).asEagerSingleton()
}