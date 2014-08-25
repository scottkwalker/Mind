name := """Mind"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.4"

// Disable documentation generation to save time for the CI build process
sources in doc in Compile := List()

libraryDependencies ++= Seq(
  //jdbc,
  //anorm,
  //cache,
  //ws
  filters,
  "org.mockito" % "mockito-core" % "1.9.5" % "test" withSources() withJavadoc(),
  "com.twitter" %% "util-eval" % "6.20.0",
  "com.google.inject" % "guice" % "4.0-beta4",
  "com.tzavellas" % "sse-guice" % "0.7.1",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalatestplus" %% "play" % "1.2.0" % "test",
  "com.twitter" %% "util-core" % "6.20.0"
)

scalacOptions ++= Seq("-feature")

ScoverageSbtPlugin.instrumentSettings

ScoverageSbtPlugin.ScoverageKeys.excludedPackages in ScoverageSbtPlugin.scoverage := "<empty>;Reverse.*"

CoverallsPlugin.coverallsSettings

incOptions := incOptions.value.withNameHashing(nameHashing = true)

showCurrentGitBranch // https://github.com/sbt/sbt-git