package utils

// Integer value that must be >= 0. This is based on an idea used in scalactic, but it is currently not
// complier-friendly with the scalatestplus dependency. It would be cool to import scalactic as it has compile-time
// checking macros.
final case class PozInt(value: Int) {

  require(value >= 0, "must be >= 0")
}
