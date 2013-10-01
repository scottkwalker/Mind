package nodes

import nodes.helpers._
import com.google.inject.Inject
import ai.Ai

case class ValueInFunctionParam(name: String, primitiveType: Node) extends Node {
  override def toRawScala: String = s"$name: ${primitiveType.toRawScala}"

  override def validate(scope: Scope): Boolean = scope.hasDepthRemaining && !name.isEmpty &&
    {primitiveType match {
      case p: IntegerM => p.validate(scope)
      case _ => false
    }}

  override def replaceEmpty(scope: Scope): Node = {
    val p = primitiveType match {
      case p: Empty => IntegerM() // TODO factory should generate the child node.
      case p: Node => p.replaceEmpty(scope)
    }
    ValueInFunctionParam(name, p)
  }
}

case class ValueInFunctionParamFactory @Inject()(creator: CreateSeqNodes,
                                                 ai: Ai) extends CreateChildNodes {
  val neighbours: Seq[CreateChildNodes] = Nil // No possible children

  override val canTerminateInStepsRemaining: Scope => Boolean = {
    def inner(f: Scope => Boolean)(scope: Scope): Boolean = scope.hasDepthRemaining
    Memoize.Y(inner)
  }

  override def create(scope: Scope): Node = {
    val name = "v" + scope.numVals

    ValueInFunctionParam(name = name,
      primitiveType = IntegerM()) // TODO need to make more types.
  }

  override def updateScope(scope: Scope) = scope.incrementVals
}


