// test Parser using Parboiled2

lazy val parboiled2Version = "2.1.3"
lazy val scalatestVersion = "2.2.6"
//lazy val nscalaTimeVersion = "2.12.0"
lazy val jodaVersion = "2.9.3"

lazy val root = (project in file(".")).
  settings(
    name := "parser",
    version := "0.1",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(
      "org.parboiled" %% "parboiled" % parboiled2Version,
      "org.scalactic" %% "scalactic" % scalatestVersion,
      "joda-time" % "joda-time" % jodaVersion,
        //      "com.github.nscala-time" %% "nscala-time" % nscalaTimeVersion,
      "org.scalatest" %% "scalatest" % scalatestVersion % "test"
    )
  )
