package memoization

import models.common.IScope
import utils.PozInt

import scala.concurrent.Future

trait Generator {

  // Calculate all values from the minimum Scope up to the maxScope. Add those that can terminate to the repository.
  def calculateAndUpdate(maxScope: IScope): Future[Int]
}