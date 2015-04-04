name := """Piranha"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.3"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "com.adrianhurt" %% "play-bootstrap3" % "0.4",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.3.0-akka-2.3.x"
)
