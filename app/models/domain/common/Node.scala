package models.domain.common

import nodes.helpers.IScope
import com.google.inject.Injector

trait Node {
  def toRaw: String

  def validate(scope: IScope): Boolean

  def replaceEmpty(scope: IScope, injector: Injector): Node

  def getMaxDepth: Int = 0

  def updateScope(scope: IScope): IScope
}