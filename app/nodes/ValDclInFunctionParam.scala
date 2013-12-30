package nodes

import nodes.helpers._
import com.google.inject.{Injector, Inject}
import ai.Ai

case class ValDclInFunctionParam(name: String, primitiveType: Node) extends Node with UpdateScopeIncrementVals {
  override def toRawScala: String = s"$name: ${primitiveType.toRawScala}"

  override def validate(scope: IScope): Boolean = scope.hasDepthRemaining && !name.isEmpty && {
    primitiveType match {
      case p: IntegerM => p.validate(scope)
      case _ => false
    }
  }

  override def replaceEmpty(scope: IScope, injector: Injector): Node = {
    val p = primitiveType match {
      case p: Empty => injector.getInstance(classOf[IntegerMFactory]).create(scope)
      case p: Node => p.replaceEmpty(updateScope(scope.incrementDepth), injector)
    }
    ValDclInFunctionParam(name, p)
  }

  override def getMaxDepth = 1 + primitiveType.getMaxDepth
}

case class ValDclInFunctionParamFactory @Inject()(injector: Injector,
                                                 creator: ICreateNode,
                                                 ai: Ai,
                                                 memoizeCanTerminateInStepsRemaining: MemoizeDi) extends ICreateChildNodes with UpdateScopeIncrementVals {
  val neighbours: Seq[ICreateChildNodes] = Seq(injector.getInstance(classOf[IntegerMFactory]))

/*  override val canTerminateInStepsRemaining: IScope => Boolean = {
    def inner(f: IScope => Boolean)(scope: IScope): Boolean = scope.hasDepthRemaining
    Memoize.Y(inner)
  }*/
  override def canTerminateInStepsRemaining(scope: IScope): Boolean = {
    val result = scope.hasDepthRemaining
    memoizeCanTerminateInStepsRemaining.store getOrElseUpdate(scope, result)
  }

  override def create(scope: IScope): Node = {
    val name = "v" + scope.numVals

    val (_, primitiveType) = creator.create(legalNeighbours(scope), scope, ai)

    ValDclInFunctionParam(name = name,
      primitiveType = primitiveType) // TODO need to make more types.
  }
}