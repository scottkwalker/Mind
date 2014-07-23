name := """Mind"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  //jdbc,
  //anorm,
  //cache,
  //ws
  "org.mockito" % "mockito-all" % "1.9.5" % "test" withSources() withJavadoc(),
  "com.twitter" % "util-eval_2.10" % "6.18.0",
  "com.google.inject" % "guice" % "4.0-beta4",
  "com.tzavellas" % "sse-guice" % "0.7.1",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "com.twitter" % "util-core_2.10" % "6.18.0",
  "org.scala-lang" %% "scala-pickling" % "0.8.0"
)

ScoverageSbtPlugin.instrumentSettings

CoverallsPlugin.coverallsSettings

incOptions := incOptions.value.withNameHashing(nameHashing = true)