name := """Piranha"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.3"

libraryDependencies ++= Seq(
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.2-SNAPSHOT"
)
