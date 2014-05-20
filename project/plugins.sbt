// Comment to get more information during initialization
logLevel := Level.Debug

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Boundary Public Repo" at "http://maven.boundary.com/artifactory/repo"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.2")

addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.5")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.4.0")