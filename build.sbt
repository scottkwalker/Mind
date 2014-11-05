import sbt.Keys.testOnly

name := "Mind"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

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
  "org.mockito" % "mockito-core" % "1.10.8" % "test" withSources() withJavadoc(),
  "com.google.inject" % "guice" % "4.0-beta5",
  "org.scalatestplus" %% "play" % "1.2.0" % "test",
  "com.twitter" %% "util-core" % "6.22.1",
  "org.scala-lang" % "scala-compiler" % "2.11.4",
  "org.scala-lang.modules" %% "scala-async" % "0.9.2"
)

scalacOptions ++= Seq("-feature")

instrumentSettings

ScoverageKeys.excludedPackages in ScoverageCompile := "<empty>;Reverse.*;fitness.Eval;views;views.html;views.html.widgets"

ScoverageKeys.minimumCoverage := 80

ScoverageKeys.failOnMinimumCoverage := true

ScoverageKeys.highlighting := true

CoverallsPlugin.coverallsSettings

incOptions := incOptions.value.withNameHashing(nameHashing = true)

showCurrentGitBranch // https://github.com/sbt/sbt-git

fork in Test := false 	// Fixes Exception in thread "Thread-4" java.io.EOFException
			//	at java.io.ObjectInputStream$BlockDataInputStream.peekByte(ObjectInputStream.java:2601)