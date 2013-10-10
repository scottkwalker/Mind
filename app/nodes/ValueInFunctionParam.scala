package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.Ai
import com.google.inject.util.Modules

case class ValueInFunctionParam(name: String, primitiveType: Node) extends Node with UpdateScopeIncrementVals {
  override def toRawScala: String = s"$name: ${primitiveType.toRawScala}"

  override def validate(scope: Scope): Boolean = scope.hasDepthRemaining && !name.isEmpty &&
    {primitiveType match {
      case p: IntegerM => p.validate(scope)
      case _ => false
    }}

  override def replaceEmpty(scope: Scope, injector: Injector): Node = {
    val p = primitiveType match {
      case p: Empty => injector.getInstance(classOf[IntegerMFactory]).create(scope)
      case p: Node => p.replaceEmpty(updateScope(scope.incrementDepth), injector)
    }
    ValueInFunctionParam(name, p)
  }

  override def getMaxDepth = 1 + primitiveType.getMaxDepth
}

case class ValueInFunctionParamFactory @Inject()(injector: Injector,
                                                 creator: CreateNode,
                                                 ai: Ai) extends CreateChildNodes with UpdateScopeIncrementVals {
  val neighbours: Seq[CreateChildNodes] = Seq(injector.getInstance(classOf[IntegerMFactory]))

  override val canTerminateInStepsRemaining: Scope => Boolean = {
    def inner(f: Scope => Boolean)(scope: Scope): Boolean = scope.hasDepthRemaining
    Memoize.Y(inner)
  }

  override def create(scope: Scope): Node = {
    val name = "v" + scope.numVals

    val (_, primitiveType) = creator.create(legalNeighbours(scope), scope, ai)

    ValueInFunctionParam(name = name,
      primitiveType = primitiveType) // TODO need to make more types.
  }
}