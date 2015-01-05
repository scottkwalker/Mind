package memoization

import models.common.IScope

import scala.concurrent.Future

trait Generator {

  def generate(maxScope: IScope): Future[Boolean]
}