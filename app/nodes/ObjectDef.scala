package nodes

import nodes.helpers._
import nodes.helpers.Scope
import com.google.inject.Injector
import com.google.inject.Inject
import ai.Ai
import scala.util.Random
import scala.annotation.tailrec

case class ObjectDef(nodes: Seq[Node], name: String) extends Node with UpdateScopeIncrementObjects {
  override def toRawScala: String = s"object $name ${nodes.map(f => f.toRawScala).mkString("{ ", " ", " }")}"

  override def validate(scope: Scope): Boolean = if (scope.hasDepthRemaining) {
    nodes.forall {
      case n@_ => n.validate(scope.incrementDepth)
      case _: Empty => false
      case _ => false
    }
  }
  else false

  override def replaceEmpty(scope: Scope, injector: Injector): Node = {
    val (_, n) = replaceEmptyInSeq(scope, injector, nodes, funcCreateNodes)
    ObjectDef(n, name)
  }

  private def funcCreateNodes(scope: Scope, injector: Injector, premade: Seq[Node]): (Scope, Seq[Node]) = {
    val factory = injector.getInstance(classOf[ObjectDefFactory])
    factory.createNodes(scope = scope, acc = premade.init)
  }

  @tailrec
  private def replaceEmptyInSeq(scope: Scope, injector: Injector, n: Seq[Node], f: ((Scope, Injector, Seq[Node]) => (Scope, Seq[Node])), acc: Seq[Node] = Seq[Node]()): (Scope, Seq[Node]) = {
    n match {
      case x :: xs => {
        val (updatedScope, replaced) = x match {
          case _: Empty => {
            f(scope, injector, n)
          }
          case n: Node => {
            val r = n.replaceEmpty(scope, injector)
            val u = r.updateScope(scope)
            (u, Seq(r))
          }
        }
        replaceEmptyInSeq(updatedScope, injector, xs, f, acc ++ replaced)
      }
      case nil => (scope, acc)
    }
  }

  override def getMaxDepth: Int = 1 + nodes.map(_.getMaxDepth).reduceLeft(math.max)
}

case class ObjectDefFactory @Inject()(injector: Injector,
                                    creator: CreateSeqNodes,
                                    ai: Ai,
                                    rng: Random) extends CreateChildNodes with UpdateScopeIncrementObjects {
  val neighbours: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[FunctionMFactory]))

  override def create(scope: Scope): Node = {
    // TODO create object level val nodes?

    val (_, nodes) = createNodes(scope)

    ObjectDef(nodes = nodes,
      name = "o" + scope.numObjects)
  }

  def createNodes(scope: Scope, acc: Seq[Node] = Seq()) = creator.createSeq(
    possibleChildren = legalNeighbours(scope),
    scope = scope,
    saveAccLengthInScope = Some((s: Scope, accLength: Int) => s.setNumFuncs(accLength)),
    acc = acc,
    factoryLimit = scope.maxFuncsInObject
  )
}