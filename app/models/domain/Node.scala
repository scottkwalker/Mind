package models.domain

import com.google.inject.Injector
import models.common.IScope

trait Node {

  def toRaw: String

  def validate(scope: IScope): Boolean

  def replaceEmpty(scope: IScope)(implicit injector: Injector): Node

  def getMaxDepth: Int

  def updateScope(scope: IScope): IScope
}