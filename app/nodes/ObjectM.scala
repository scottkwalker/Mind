package nodes

import nodes.helpers._
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai

case class ObjectM(nodes: Seq[Node], name: String) extends Node {
  override def toRawScala: String = s"object $name ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"

  override def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) false
  else nodes.forall(n => n match {
    case _: FunctionM => n.validate(scope.decrementStepsRemaining)
    case _: Empty => false
    case _ => false
  })
}

case class ObjectMFactory @Inject()(injector: Injector,
                                    creator: CreateSeqNodes,
                                    ai: Ai) extends CreateChildNodes {
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
    ai = ai,
    constraints = (s: Scope, accLength: Int) => accLength < s.maxFuncsInObject,
    updateScopeWithAcc = (s: Scope, accLength: Int) => s.setNumFuncs(accLength)
  )

  override def updateScope(scope: Scope) = scope.incrementObjects
}