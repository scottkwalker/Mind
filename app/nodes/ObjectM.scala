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
}

case class ObjectMFactory @Inject()(injector: Injector,
                                    creator: CreateSeqNodes,
                                    ai: Ai,
                                    rng: Random) extends CreateChildNodes {
  val neighbours: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[FunctionMFactory]))

  override def create(scope: Scope, premade: Option[Node] = None): Node = {
    // TODO create object level val nodes?

    val (_, nodes) = createNodes(scope, premade)

    ObjectM(nodes = nodes,
      name = "o" + scope.numObjects)
  }

  private def createNodes(scope: Scope, premade: Option[Node]) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    ai = ai,
    constraints = (s: Scope, accLength: Int) => accLength < 1 + rng.nextInt(s.maxFuncsInObject),
    saveAccLengthInScope = (s: Scope, accLength: Int) => s.setNumFuncs(accLength),
    acc = Seq[Node](),
    premade
  )

  override def updateScope(scope: Scope) = scope.incrementObjects
}