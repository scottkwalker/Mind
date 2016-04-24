//SBT plugins used by the project build including Play itself.

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Boundary Public Repo" at "http://maven.boundary.com/artifactory/repo"

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.7")

//addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.4.0")

// Plugin for publishing scoverage results to coveralls
resolvers += Classpaths.sbtPluginReleases

// Plugin for publishing scoverage results to coveralls
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.0.4")

addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "1.0.0.BETA1")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.0")

//addSbtPlugin("com.typesafe" % "sbt-abide" % "0.1-SNAPSHOT")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

addSbtPlugin("org.brianmckenna" % "sbt-wartremover" % "0.13")