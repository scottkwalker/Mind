// Comment to get more information during initialization
logLevel := Level.Debug

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "Boundary Public Repo" at "http://maven.boundary.com/artifactory/repo"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.2")