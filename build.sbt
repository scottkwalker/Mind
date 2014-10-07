import sbt.Keys.testOnly

name := "Mind"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.2"

// Disable documentation generation to save time for the CI build process
sources in doc in Compile := List()

// For Travis-CI the log file has a limit of 4MB. We broke a build with the amount of debug that sbt produces loading a
// project from scratch, so now only output when there is a warning or error.
logLevel := Level.Warn

logLevel in testOnly := Level.Info

logLevel in testQuick := Level.Info

libraryDependencies ++= Seq(
  //jdbc,
  //anorm,
  //cache,
  //ws
  filters,
  "org.mockito" % "mockito-core" % "1.9.5" % "test" withSources() withJavadoc(),
  "com.google.inject" % "guice" % "4.0-beta4",
  "org.scalatestplus" %% "play" % "1.2.0" % "test",
  "com.twitter" %% "util-core" % "6.20.0",
  "org.scala-lang" % "scala-compiler" % "2.11.2"
)

scalacOptions ++= Seq("-feature")

instrumentSettings

ScoverageKeys.excludedPackages in ScoverageCompile := "<empty>;Reverse.*;composition.Eval;views;views.html;views.html.widgets"

ScoverageKeys.minimumCoverage := 80

ScoverageKeys.failOnMinimumCoverage := true

ScoverageKeys.highlighting := true

CoverallsPlugin.coverallsSettings

incOptions := incOptions.value.withNameHashing(nameHashing = true)

showCurrentGitBranch // https://github.com/sbt/sbt-git