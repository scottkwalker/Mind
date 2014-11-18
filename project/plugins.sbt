//SBT plugins used by the project build including Play itself.

logLevel := Level.Debug

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Boundary Public Repo" at "http://maven.boundary.com/artifactory/repo"

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.4")

//addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.5")

//addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.4.0")

// Plugin for publishing scoverage results to coveralls
resolvers += Classpaths.sbtPluginReleases

// Plugin for publishing scoverage results to coveralls
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "0.99.7.1")

//addSbtPlugin("com.sksamuel.scoverage" %% "sbt-coveralls" % "0.0.5")

addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "0.98.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.6.4")