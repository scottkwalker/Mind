package models.domain

import com.google.inject.Injector
import models.common.IScope
import models.domain.scala.FactoryLookup

import _root_.scala.concurrent.Future

// Represents a unit of a language, e.g. arithmetic (+ -), logic operations (&& ||), control flow (if-else), etc
trait Step {

  // Convert the Instruction into a raw code for its language, e.g. an add operator can be represented as + in Scala. If
  // the instruction has an arity > 0 then it will have to recursively call the child instructions to get their string
  // representation.
  def toCompilable: String

  // Returns false if any child of this instruction is of type Empty, meaning that the tree is not in a state where it
  // can be output as raw code.
  def hasNoEmptySteps(scope: IScope): Boolean

  // Recursively replace any child Instruction of type Empty with new values chosen by the AI.
  def fillEmptySteps(scope: IScope, factoryLookup: FactoryLookup): Future[Step]

  // The height of the tree. Minimum of 1 (the current node).
  def height: Int

  def updateScope(scope: IScope): IScope
}