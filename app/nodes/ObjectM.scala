package nodes

import nodes.helpers._
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai
import scala.util.Random

case class ObjectM(nodes: Seq[Node], name: String) extends Node {
  override def toRawScala: String = s"object $name ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"

  override def validate(scope: Scope): Boolean = if (scope.hasDepthRemaining) {
    nodes.forall(n => n match {
      case _: FunctionM => n.validate(scope.incrementDepth)
      case _: Empty => false
      case _ => false
    })
  }
  else false

  override def replaceEmpty(scope: Scope, injector: Injector): Node = {
    val n = nodes.map(n => replaceEmpty(scope, injector, n))
    ObjectM(n, name)
  }

  private def replaceEmpty(scope: Scope, injector: Injector, n: Node): Node = {
    n match {
      case _: Empty => injector.getInstance(classOf[ObjectMFactory]).create(scope)
      case n: Node => n.replaceEmpty(scope, injector)
    }
  }
}

case class ObjectMFactory @Inject()(injector: Injector,
                                    creator: CreateSeqNodes,
                                    ai: Ai,
                                    rng: Random) extends CreateChildNodes with ObjectMUpdateScope {
  val neighbours: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[FunctionMFactory]))

  override def create(scope: Scope): Node = {
    // TODO create object level val nodes?

    val (_, nodes) = createNodes(scope)

    ObjectM(nodes = nodes,
      name = "o" + scope.numObjects)
  }

  private def createNodes(scope: Scope) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    saveAccLengthInScope = Some((s: Scope, accLength: Int) => s.setNumFuncs(accLength)),
    factoryLimit = scope.maxFuncsInObject
  )
}

trait ObjectMUpdateScope extends UpdateScope {
  override def updateScope(scope: Scope): Scope = scope.incrementObjects
}