name := """Piranha"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  ws
)

libraryDependencies ++= Seq(
  //"org.webjars" % "jquery" % "2.1.3",
  //"org.webjars" % "bootstrap" % "3.3.4",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.3.0-akka-2.3.x",
  "org.seleniumhq.webdriver" % "webdriver-htmlunit" % "0.9.7376",
  "org.mongodb" %% "casbah" % "2.8.0",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.6",
  "org.apache.commons" % "commons-email" % "1.2",
  "io.spray" %%  "spray-json" % "1.3.1"
)