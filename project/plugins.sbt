//SBT plugins used by the project build including Play itself.

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Boundary Public Repo" at "http://maven.boundary.com/artifactory/repo"

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.7")

// Plugin for publishing scoverage results to coveralls
resolvers += Classpaths.sbtPluginReleases

// Plugin for publishing scoverage results to coveralls
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")

addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "1.0.0.BETA1")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.0")

//addSbtPlugin("com.typesafe" % "sbt-abide" % "0.1-SNAPSHOT")


/////////////////////////////////////
// scalafmt
//
// support for source code formatting
// https://github.com/olafurpg/scalafmt
// Run with 'sbt scalafmt'

addSbtPlugin("com.geirsson" %% "sbt-scalafmt" % "0.2.3")

// End scalafmt
/////////////////////////////////////

addSbtPlugin("org.brianmckenna" % "sbt-wartremover" % "0.14")