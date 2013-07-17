package fitness

class Addition extends Fitness {
  def fitness: Double = {
    val a = 1
    val b = 1
    val expected = 2

    val result = expected - (a + b)

    if (result == 0) 1.0 else 1 / (result.abs)
  }
}