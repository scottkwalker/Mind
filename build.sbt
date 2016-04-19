import sbt.Keys.testOnly
import scoverage.ScoverageSbtPlugin.ScoverageKeys

name := "Mind"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

// Disable documentation generation to save time for the CI build process
sources in doc in Compile := List()

// For Travis-CI the log file has a limit of 4MB. We broke a build with the amount of debug that sbt produces loading a
// project from scratch, so now only output when there is a warning or error.
logLevel := Level.Warn

logLevel in test := Level.Info

logLevel in testOnly := Level.Info

logLevel in testQuick := Level.Info

libraryDependencies ++= Seq(
  //jdbc,
  //anorm,
  //cache,
  //ws
  filters,
  "org.mockito" % "mockito-core" % "2.0.5-beta" % "test",// withSources() withJavadoc(),
  "com.google.inject" % "guice" % "4.0-beta5",// withSources() withJavadoc(),
  "org.scalatestplus" %% "play" % "1.2.0" % "test",// withSources() withJavadoc(),
  "com.twitter" %% "util-core" % "6.22.1",// withSources() withJavadoc(),
  "org.scala-lang" % "scala-compiler" % "2.11.8",// withSources() withJavadoc(),
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",// withSources() withJavadoc(),
//  "org.scalactic" %% "scalactic" % "2.2.1",// withSources() withJavadoc(),
//  "com.typesafe" %% "abide-core" % "0.1-SNAPSHOT" % "abide",
  "org.scala-lang" % "scala-compiler" % scalaVersion.value
)

scalacOptions += "-feature"

ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;fitness.Eval;views;views.html;views.html.widgets"

ScoverageKeys.coverageMinimum := 80

ScoverageKeys.coverageFailOnMinimum := true

ScoverageKeys.coverageHighlighting := true

incOptions := incOptions.value.withNameHashing(nameHashing = true)

showCurrentGitBranch // https://github.com/sbt/sbt-git

fork in Test := false 	// Fixes Exception in thread "Thread-4" java.io.EOFException
			//	at java.io.ObjectInputStream$BlockDataInputStream.peekByte(ObjectInputStream.java:2601)

autoCompilerPlugins := true

/////////////////////////////////////
// Scalaxy
//
// Scalaxy/Streams compiler plugin
// https://github.com/ochafik/Scalaxy

// Ensure Scalaxy/Streams's plugin is used.
scalacOptions += "-Xplugin-require:scalaxy-streams"

scalacOptions ++= Seq("-optimise", "-Yinline-warnings", "-Yclosure-elim", "-Yinline", "-Ybackend:GenBCode")

scalacOptions in Test ~= (_ filterNot (_ == "-Xplugin-require:scalaxy-streams"))

scalacOptions in Test += "-Xplugin-disable:scalaxy-streams"

addCompilerPlugin("com.nativelibs4java" %% "scalaxy-streams" % "0.3.4")

// End Scalaxy
/////////////////////////////////////


/////////////////////////////////////
// scalariform
//
// support for source code formatting
// https://github.com/sbt/sbt-scalariform
// Run with 'sbt scalariformFormat'

import scalariform.formatter.preferences._

scalariformSettings

// End scalariform
/////////////////////////////////////
