package models.domain.common

import com.google.inject.Injector
import nodes.helpers.IScope

trait Node {

  def toRaw: String

  def validate(scope: IScope): Boolean

  def replaceEmpty(scope: IScope, injector: Injector): Node

  def getMaxDepth: Int = 0

  def updateScope(scope: IScope): IScope
}