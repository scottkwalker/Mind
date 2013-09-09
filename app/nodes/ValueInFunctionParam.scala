package nodes

import nodes.helpers._
import com.google.inject.Inject

case class ValueInFunctionParam(name: String, primitiveType: Node) extends Node {
  override def toRawScala: String = s"$name: ${primitiveType.toRawScala}"

  override def validate(scope: Scope): Boolean = !name.isEmpty &&
    (primitiveType match {
      case p: IntegerM => p.validate(scope)
      case _ => false
    })
}

case class ValueInFunctionParamFactory @Inject()() extends CreateChildNodes {
  val neighbours: Seq[CreateChildNodes] = Nil // No possible children

  override val canTerminateInStepsRemaining: Scope => Boolean = {
    def inner(f: Scope => Boolean)(scope: Scope): Boolean = {
      if (scope.noStepsRemaining) false else true
    }
    Memoize.Y(inner)
  }

  override def create(scope: Scope): Node = {
    val name = "v" + scope.numVals
    ValueRef(name = name)
  }

  override def updateScope(scope: Scope) = scope.incrementVals
}


