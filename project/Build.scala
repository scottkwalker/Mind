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
	"org.specs2" %% "specs2" % "1.14" % "test",
	"com.twitter" % "util-eval_2.10" % "6.3.7" withSources(),
	"org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
	"org.mockito" % "mockito-all" % "1.9.5" % "test" withSources() withJavadoc(),
	"com.google.inject" % "guice" % "3.0" withSources() withJavadoc(),
	"com.tzavellas" % "sse-guice" % "0.7.1" withSources() withJavadoc()
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
	// Add your own project settings here  
	resolvers ++= Seq(
		"snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
		"releases" at "http://oss.sonatype.org/content/repositories/releases"
    )    
  )

}
