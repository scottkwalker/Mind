package memoization

import com.google.inject.Inject

import scala.concurrent.Future

class GeneratorImpl @Inject()() extends Generator {

  def generate: Future[Boolean] = Future.successful(true)
}
