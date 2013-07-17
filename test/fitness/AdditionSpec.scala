package fitness

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import fitness.Fitness._

class AdditionSpec extends Specification {
  "Addition" should {
    "1 add 1 equals 2" in {
      val f: Fitness = new Addition
      f.fitness mustEqual maxFitness
    }
  }
}