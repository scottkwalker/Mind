import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "Mind"
  val appVersion      = "1.0-SNAPSHOT"



  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.specs2" %% "specs2" % "2.3.6" % "test" withSources() withJavadoc(),
    "org.mockito" % "mockito-all" % "1.9.5" % "test" withSources() withJavadoc(), // For specs2
    "com.twitter" % "util-eval_2.10" % "6.5.0",
    "org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
    "com.google.inject" % "guice" % "4.0-beta",
    "com.tzavellas" % "sse-guice" % "0.7.1",
    "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers ++= Seq(
      "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
      "releases" at "http://oss.sonatype.org/content/repositories/releases"
    )
  )

}
