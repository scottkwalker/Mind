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
	"com.twitter" % "util-eval_2.10" % "6.3.7" withSources()
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
