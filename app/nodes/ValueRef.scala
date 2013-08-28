package nodes

import nodes.helpers._
import com.google.inject.Inject

case class ValueRef(val name: String) extends Node {
  override def toRawScala: String = name
  override def validate(scope: Scope): Boolean = if (scope.noStepsRemaining) false else !name.isEmpty
}

case class ValueRefFactory @Inject() () extends FeasibleNodes {
  val allPossibleChildren: Seq[FeasibleNodes] = Seq() // No possible children TODO should this be Nil

  override def couldTerminate(scope: Scope) = {
    if (scope.noStepsRemaining) false else true
  }

  override def create(scope: Scope): Node = {
    val name = "v" + scope.numVals
    ValueRef(name = name)
  }
}