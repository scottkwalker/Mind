package ai.aco

import composition.TestComposition
import composition.UnitTestHelpers

trait Pheromone {

  val value: Double

  def drop(amount: Double): Pheromone

  def evaporate: Pheromone
}

final case class PheromoneConstantEvaporation(value: Double) extends Pheromone {

  override def drop(amount: Double) = this.copy(value = value + amount)

  override def evaporate = {
    val evaporationAmount = 1.0
    if (value <= evaporationAmount) this.copy(value = 0) // Lower bound of zero
    else this.copy(value = value - evaporationAmount)
  }
}

final class PheromoneSpec extends UnitTestHelpers with TestComposition {

  "drop" must {
    "return the amount dropped if value is zero" in {
      val amount = 10.0
      val pheromone = build()

      val newPheromone = pheromone.drop(amount = amount)

      newPheromone.value must equal(amount)
    }
  }

  "evaporate" must {
    "return zero if value is zero" in {
      val pheromone = build()

      val newPheromone = pheromone.evaporate

      newPheromone.value must equal(0.0)
    }

    "return a decreased amount" in {
      val startValue = 10.0
      val pheromone = build(value = startValue)

      val newPheromone = pheromone.evaporate

      newPheromone.value must equal(9.0)
    }
  }

  private def build(value: Double = 0.0): Pheromone = PheromoneConstantEvaporation(value = value)
}
