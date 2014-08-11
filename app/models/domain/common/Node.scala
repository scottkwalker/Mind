package models.domain.common

import com.google.inject.Injector
import nodes.helpers.IScope

trait Node {

  def toRaw: String

  def validate(scope: IScope): Boolean

  def replaceEmpty(scope: IScope)(implicit injector: Injector): Node

  def getMaxDepth: Int

  def updateScope(scope: IScope): IScope
}