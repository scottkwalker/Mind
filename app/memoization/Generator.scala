package memoization

import scala.concurrent.Future

trait Generator {

  def generate: Future[Boolean]
}