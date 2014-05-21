import sbt._
import sbt.Keys._
import play.Project._
import de.johoop.jacoco4sbt.JacocoPlugin._
import org.scalastyle.sbt.ScalastylePlugin

object Build extends sbt.Build {

  val appName = "Mind"
  val appVersion = "1.0-SNAPSHOT"

  jacoco.settings

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.mockito" % "mockito-all" % "1.9.5" % "test" withSources() withJavadoc(), // For specs2
    "com.twitter" % "util-eval_2.10" % "6.12.1",
    "org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
    "com.google.inject" % "guice" % "4.0-beta",
    "com.tzavellas" % "sse-guice" % "0.7.1",
    "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
    "com.twitter" % "util-core_2.10" % "6.12.1"
  )

  val jcoco = Seq(parallelExecution in jacoco.Config := false)

  val appSettings: Seq[Def.Setting[_]] = jcoco

  val main = play.Project(name = appName,
    applicationVersion = appVersion,
    dependencies = appDependencies,
    settings = play.Project.playScalaSettings ++
      jacoco.settings ++
      ScalastylePlugin.Settings).settings(
      // Add your own project settings here
      resolvers ++= Seq(
        "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
        "releases" at "http://oss.sonatype.org/content/repositories/releases"
      )
    )

}
